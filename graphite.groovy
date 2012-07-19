import org.apache.log4j.Category

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.fields.CustomField

log = Category.getInstance("no.startsiden.no.jira.groovy.graphite")
def team_field = "customfield_10520" // XXX: This is not pretty to hardcode?

long unixTime = System.currentTimeMillis() / 1000L;

Issue issue = issue

action = transientVars.get("descriptor").getAction(transientVars.get("actionId"))


ComponentManager componentManager = ComponentManager.getInstance()
CustomFieldManager customFieldManager = componentManager.getCustomFieldManager()
CustomField customFieldSrc = customFieldManager.getCustomFieldObject(team_field)

team = issue.getCustomFieldValue(customFieldSrc).toString().toLowerCase()

log.error("team: ${team}")
try {
    netcat = new Socket("192.168.30.23", 2003)
    netcat.withStreams { input, output ->
        msg = "events.deploy.prod.${team}.${issue.key} 1 ${unixTime}\n"
        output << "${msg}"
        netcat.close()
    }
} catch (ex) {
    log.error("Exception doing netcat to dev-rolf");
}

