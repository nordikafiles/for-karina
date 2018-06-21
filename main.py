import ssl
ssl.match_hostname = lambda cert, hostname: True
import urllib.request
import urllib.parse
import json

VK_ENDPOINT = 'https://api.vk.com/method/'
VK_ACCESS_TOKEN = '40383436280e1d91d55f82cde53186d3fdfcf64d06751cbfc34cd68b197aa7e5ed13c283a48df2b1f4c21'
VK_VERSION = '5.67'

LONG_POLL_CONFIG = None

def http_request(url):
    f = urllib.request.urlopen(url)
    result = json.loads(f.read().decode('utf-8'))
    return result

def get_long_polling_server():
    global LONG_POLL_CONFIG
    LONG_POLL_CONFIG = http_request(VK_ENDPOINT + 'messages.getLongPollServer?access_token=' + VK_ACCESS_TOKEN + '&v=' + VK_VERSION + '&group_id=')['response']
    return LONG_POLL_CONFIG


def long_poll():
    get_long_polling_server()
    while True:
        
        result = http_request('https://' + LONG_POLL_CONFIG['server'] + '?act=a_check&key=' + str(LONG_POLL_CONFIG['key']) + '&ts=' + str(LONG_POLL_CONFIG['ts']) + '&wait=25')
        LONG_POLL_CONFIG['ts'] = result['ts']
        if 'updates' in result:
            for update in result['updates']:
                print(update)
                if len(update) > 4 and update[0] == 4 and update[2] != 3:
                    message = update[6]
                    user = update[3]
                    print(user, message)
                    send_message(user, 'Все говорят "' + message + '", а ты купи слона!')

def send_message(user, message):
    message = urllib.parse.quote(message.encode("utf-8"))
    print(http_request(VK_ENDPOINT + 'messages.send?access_token=' + VK_ACCESS_TOKEN + '&v=' + VK_VERSION + '&peer_id=' + str(user) + '&message=' + message))

long_poll()