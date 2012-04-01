import com.atlassian.jira.issue.Issue
Issue issue = issue

netcat = new Socket("noops1.startsiden.no", 54321)
netcat.withStreams { input, output ->
  output << "#drift ${issue.key} ${issue.summry} \n"
  buffer = input.newReader().readLine()
}
