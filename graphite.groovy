import org.apache.log4j.Category

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.fields.CustomField

log = Category.getInstance("no.startsiden.no.jira.groovy.graphite")
// XXX: These thingd are hard-coded here, because I haven't found a good way to get
// config from anywhere for script runner scripts
def hosts = ["192.168.30.23", "192.168.30.113"]
def logstash_hosts = ["192.168.30.113"]
def logstash_port = 10514

def team_field = "customfield_10520"
stages = [
    41:"prod",
    21:"kua",
]

long unixTime = System.currentTimeMillis() / 1000L; // / # Vim fuckups!

Issue issue = issue

action = transientVars.get("actionId")



ComponentManager componentManager = ComponentManager.getInstance()
CustomFieldManager customFieldManager = componentManager.getCustomFieldManager()
CustomField customFieldSrc = customFieldManager.getCustomFieldObject(team_field)

team = issue.getCustomFieldValue(customFieldSrc).toString().toLowerCase()
stage = stages.get(action)

log.error("team: ${team}, action: ${action} stage: ${stage} issue: ${issue.key}")
for (host in hosts)
    try {
        netcat = new Socket(host, 2003)
        netcat.withStreams { input, output ->
            msg = "events.deploy.${stage}.${team} 1 ${unixTime}\n"
            output << "${msg}"
            netcat.close()
        }
        netcat = new Socket(host, 2003)
        netcat.withStreams { input, output ->
            msg = "events.deploy.${stage}.${team}.${issue.key} 1 ${unixTime}\n"
            netcat.close()
        }
    } catch (ex) {
        log.error("Exception doing netcat to ${host}: ${ex}");
    }

for (host in logstash_hosts)
    try {
        nc = new Socket(host, logstash_port)
        nc.withStreams { input, output ->
            msg = "${now} DEPLOY ${stage} ${team} ${issue.key}"
            output << msg
            nc.close()
        }
    } catch (ex) {
        println "error communicating with ${host}: ${ex}"
    }
