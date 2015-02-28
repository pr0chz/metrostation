import sys
import json

for line in sys.stdin.readlines():
  cell = json.loads(line.strip())
  try:
    gsm = cell['cellLocation']['gsm']
    print json.dumps({'ts': cell['dumpTS'], 'cid':gsm['cid'], 'lac':gsm['lac']})
  except:
    print json.dumps({'ts': cell['dumpTS']})
