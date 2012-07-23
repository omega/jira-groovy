import org.apache.log4j.Category

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.fields.CustomField

log = Category.getInstance("no.startsiden.no.jira.groovy.graphite")
def team_field = "customfield_10520" // XXX: This is not pretty to hardcode?
stages = [
    41:"prod",
    21:"kua",
]

long unixTime = System.currentTimeMillis() / 1000L;

Issue issue = issue

action = transientVars.get("actionId")



ComponentManager componentManager = ComponentManager.getInstance()
CustomFieldManager customFieldManager = componentManager.getCustomFieldManager()
CustomField customFieldSrc = customFieldManager.getCustomFieldObject(team_field)

team = issue.getCustomFieldValue(customFieldSrc).toString().toLowerCase()
stage = stages.get(action)

log.error("team: ${team}, action: ${action} stage: ${stage}")
try {
    netcat = new Socket("192.168.30.23", 2003)
    netcat.withStreams { input, output ->
        msg = "events.deploy.${stage}.${team}.${issue.key} 1 ${unixTime}\n"
        output << "${msg}"
        msg = "events.deploy.${stage}.${team} 1 ${unixTime}\n"
        output << "${msg}"
        netcat.close()
    }
} catch (ex) {
    log.error("Exception doing netcat to dev-rolf: ${ex}");
}

