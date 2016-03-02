#!/usr/bin/python
#parse url and direct to either query 1 or query 2
#currently only for query 1

from urlparse import urlparse, parse_qs
import datetime
import decryption

TeamID = "TEAMID"
TeamAWSID = "TEAM_AWS_ACCOUNT_ID"
#url = "www.ourmachine.com/q1?key=306063896731552281713201727176392168770237379582172677299123272033941091616817696059536783089054693601&message=URYYBBJEX"

def handleRequest(url):
    currentTime = datetime.datetime.strftime(datetime.datetime.now(), '%Y-%m-%d %H:%M:%S')
    teamTimeInfo = TeamID + ',' + TeamAWSID + '\n' + currentTime
    query = parse_qs(urlparse(url).query)
    
    if 'key' not in query or 'message' not in query:
        return teamTimeInfo
    
    key = query['key'][0]
    C = query['message'][0]

    if key == None or C == None:
        return teamTimeInfo

    return teamTimeInfo + '\n' + decryption.getDecodedM(key, C)

    
