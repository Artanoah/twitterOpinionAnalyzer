from pprint import pprint

import requests

import json


r = requests.get(r'https://www.reddit.com/r/all/comments/.json')

data = r.json()

print data.keys()
print data.values()

for child in data['data']['children']:

    print child['data']['id'], "\r\n", child['data']['author'],child['data']['body']

    print