import os
import requests
from datetime import datetime
from datetime import timedelta

from configuration import ConfServer

headers = {
    'X-API-Key': ConfServer.api,
    'Content-Type': "application/xml",
    'Accept': "*/*",
    'Cache-Control': "no-cache",
    'Accept-Encoding': "gzip, deflate",
    'Connection': "keep-alive",
    'cache-control': "no-cache"
    }

twoYearsAgo = datetime.now() - timedelta(days= 728)
today = datetime.now()

if not os.path.exists('reports'):
    os.makedirs('reports')

url = "http://"+ConfServer.ip+":"+ConfServer.port+"/api/v1/commits/custom/Issue/"+twoYearsAgo.strftime("%Y-%m-%d")+"/to/"+today.strftime("%Y-%m-%d")+"/csv"
response = requests.request("GET", url, headers=headers)
print(response.text)
file = open('reports/issues.csv', 'w')
file.write(response.text)
file.close