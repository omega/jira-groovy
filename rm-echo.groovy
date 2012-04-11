import org.apache.log4j.Category

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.fields.CustomField


log = Category.getInstance("no.startsiden.no.jira.groovy.rmecho")
def team_field = "customfield_10520" // XXX: This is not pretty to hardcode?
team_rooms = [
    "Football":"@fotball",
    "MeeTV":"@tvguide",
    "Navigation":"@navigation",
    "Startsiden":"@startsiden",
    "News":"@news",
    "Video":"@video",
] // XXX: this is not pretty to hardcode either

Issue issue = issue
url = "https://bugs.startsiden.no/browse/${issue.key}"

action = transientVars.get("descriptor").getAction(transientVars.get("actionId"))


ComponentManager componentManager = ComponentManager.getInstance()
CustomFieldManager customFieldManager = componentManager.getCustomFieldManager()
CustomField customFieldSrc = customFieldManager.getCustomFieldObject(team_field)

team = issue.getCustomFieldValue(customFieldSrc).toString()
room = team_rooms.get(team)

log.error("team: ${team} room: ${room}")
try {
    netcat = new Socket("noops1.startsiden.no", 54321)
        netcat.withStreams { input, output ->
            msg = "'${issue.key}: ${issue.summary}' ${action.name} Next up: ${issue.getAssigneeUser().getDisplayName()} ${url}\n"
                output << "${room},#drift ${msg}"
                buffer = input.newReader().readLine()
        }
} catch (ex) {
    log.error("Exception doing netcat to noops1");
}
