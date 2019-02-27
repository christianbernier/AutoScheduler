# AutoScheduler
This program is designed to automatically send scheduling emails to those scheduled for one of two church services. It pulls data from a public Google Sheet, parses the JSON, then sends the emails using the javax.mail library.

## Files Included
| File           | Purpose            |
| ---------------| -------------------|
| TLSEmail.java  | Main program file  |
| Gson.java      | JSON parser        |
| EmailUtil.java | Sends emails       |
| creds.config   | Sender credentials |
