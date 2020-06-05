import requests

from configuration import ConfServer

headers = {
    'X-API-Key': ConfServer.api,
    'cache-control': "no-cache",
    }

ip = ConfServer.ip
port = ConfServer.port

url = "http://"+ip+":"+port+"/boot/"+ConfServer.magic
response = requests.request("POST", url, headers=headers)
print(response.text)

