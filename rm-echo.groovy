import com.atlassian.jira.issue.Issue
Issue issue = issue
action = transientVars.get("descriptor").getAction(transientVars.get("actionId"))

netcat = new Socket("noops1.startsiden.no", 54321)
netcat.withStreams { input, output ->
  output << "#drift ${issue.key} ${issue.summary} ${action.name} ${issue.getAssigneeUser().getDisplayName()}\n"
  buffer = input.newReader().readLine()
}
