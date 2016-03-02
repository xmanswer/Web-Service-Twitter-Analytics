import SocketServer
import string
import time
import math

class MyTCPHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        self.data = self.request.recv(1024).strip()
        #print self.data
	startIndex = self.data.index('GET')
	endIndex = self.data.index('HTTP/')
	substring = self.data[startIndex+5:endIndex-1]
	self.data = 'Hello World';
        #print substring
        strings = substring.split('?')
        if (strings[0] == 'q1'):
	  message = self.getMessage(strings[1])
	  result = 'theImp,4371-1035-2488\n' + time.strftime("%Y-%m-%d %H:%M:%S") + '\n'+ message + '\n'
          self.request.sendall(result)
	else:
	  self.request.sendall("Hello World")

    def getMessage(self, inputMessage):
	paras = inputMessage.split('&')
	data = {'key':0, 'message':''}
	for para in paras:
	  kvs = para.split('=')
	  if (len(kvs) < 2):
	    continue
	  data[kvs[0]] = kvs[1]
	key = data['key']
	message = data['message']
	teamKey = 8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773
        number = long(key) / teamKey
	number = number % 25 + 1
	messageLength = len(message)
	output = "";
	n = math.sqrt(messageLength)
	n = int(n)
	int_of_A = ord('A')
	for s in range(2, n + 2):
	  for i in range(1, s):
	    int_of_char = ord(message[s + 2 * i - 4])
	    new_char = (int_of_char - number + 26 - int_of_A) % 26 + int_of_A
	    output = output + chr(new_char)

	for s in range(n + 2, n * 2 + 1):
	  for i in range(s - n, n + 1):
	    int_of_char = ord(message[s + 2 * i - 4])
	    new_char = (int_of_char - number + 26 - int_of_A) % 26 + int_of_A
	    output = output + chr(new_char)
	#print key
	#print number
	#print message
	return output


if __name__ == "__main__":
    HOST, PORT = "0.0.0.0", 80
    SocketServer.ThreadingTCPServer.allow_reuse_address = True
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)
    server.serve_forever()
