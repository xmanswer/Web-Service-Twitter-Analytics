#!/usr/bin/python
#handling requests for Heartbeat and Authentication for query 1

from decimal import Decimal
import math

#decode given key string and encoded message C to real message M
def getDecodedM(key, C):  
    X = "8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773"
    Y = Decimal(key) / Decimal(X)
    Z = 1 + Y % 25
    n = int(math.sqrt(len(C)))

    I = str() #intermediate string 
    M = str() #final string

    #generate list of indexes with correct order
    indexList = [] 
    for line in range(1, n + 1):
        for i in range(line):
            indexList.append(line + i * (n - 1))

    for line in range(2, n + 1):
        for i in range(n - line + 1):
            indexList.append(line * n + i * (n - 1))

    for index in indexList:
        I = I + C[index - 1]

    for i in I:
        ascii = ord(i) - Z
        if ascii < ord('A'):
            ascii = ascii + 26
        M = M + chr(ascii)

    return M
