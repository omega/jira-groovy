import org.apache.log4j.Category

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.fields.CustomField

log = Category.getInstance("no.startsiden.no.jira.groovy.rmecho")
def team_field = "customfield_10520" // XXX: This is not pretty to hardcode?
team_rooms = [
    "Fotball":"@fotball",
    "MeeTV":"@tvguide",
    "Navigation":"@navigation",
    "Startsiden":"@startsiden",
    "News":"@news",
    "Video":"@video",
] // XXX: this is not pretty to hardcode either

Issue issue = issue
action = transientVars.get("descriptor").getAction(transientVars.get("actionId"))


ComponentManager componentManager = ComponentManager.getInstance()
CustomFieldManager customFieldManager = componentManager.getCustomFieldManager()
CustomField customFieldSrc = customFieldManager.getCustomFieldObject(team_field)

team = issue.getCustomFieldValue(customFieldSrc).toString()
room = team_rooms.get(team)

log.info("team: ${team} room: ${room}")

netcat = new Socket("noops1.startsiden.no", 54321)
netcat.withStreams { input, output ->
  output << "${room},#drift ${issue.key}: ${issue.summary} (${team}) ${action.name} Next up: ${issue.getAssigneeUser().getDisplayName()}\n"
  buffer = input.newReader().readLine()
}
