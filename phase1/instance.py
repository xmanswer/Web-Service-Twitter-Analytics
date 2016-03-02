#!/usr/bin/python

from paste import httpserver
import bottle
from bottle import get, error, post, request, run
import handleRequest


# Handle http GET requests
@get('/q1')
def query1():
    url = request.url
    r = ''
    try:
        r = handleRequest.handleRequest(url)
    except:
        pass
    return r

@error(403)
def error1(code):
    return '403'
 
@error(404)
def error2(code):
    return '404'
 
# Use paste (multi-threaded server) instead of built-in single threaded server
application = bottle.default_app()
# Listen to HTTP requests on all interfaces
httpserver.serve(application, host='0.0.0.0', port=80, request_queue_size=1000)

