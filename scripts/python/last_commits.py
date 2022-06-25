import requests

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

url = "http://"+ConfServer.ip+":"+ConfServer.port+"/api/v1/commits"
response = requests.request("GET", url, headers=headers)
print(response.text)