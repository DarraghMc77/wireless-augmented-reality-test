import sys
import socket
from darkflow.net.build import TFNet
import cv2
import json
import numpy
import time

CONN_QUEUE = 50

def main():
    # options = {"model": "cfg/yolo.cfg", "load": "bin/yolo.weights", "threshold": 0.1}
    options = {"model": "cfg/yolo.cfg", "load": "bin/yolo.weights", "threshold": 0.1, "gpu": 1.0}

    tfnet = TFNet(options)

    host = "192.168.6.126"
    port = 8033
    print "Server Running on port: ", port

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind((host, port))
    sock.listen(CONN_QUEUE)

    while True:
        print >> sys.stderr, 'waiting for a connection'
        connection, client_address = sock.accept()

        try:
            print >> sys.stderr, 'connection from', client_address
            img = ""
            data = connection.recv(4096)
            # try:
            txt = data.decode('utf-8')
            amount_received = 0

            # Get size of image byte[]
            if txt.startswith("SIZE"):
                size = int(txt.split()[1])
                print(size)
            while True:
                # print(amount_received)
                data = connection.recv(4096)
                amount_received += len(data)
                img += data
                if(amount_received >= size):
                    break
            print("Image received")

            start = time.time()
            nparr = numpy.fromstring(img, numpy.uint8)
            img_np = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

            # fh = open("./testImage.jpg", "wb")
            # fh.write(img)
            # fh.close()

            # Run YOLO prediction
            # imgcv = cv2.imread("./testImage.jpg")
            result = tfnet.return_predict(img_np)
            print(result)

            print(start - time.time())

            for obj in result:
                print(type(obj['confidence']))
                obj['confidence'] = str(obj['confidence'])

            print str(result[0])

            # only returning first one
            json_result = json.dumps(result[0])
            print(json_result)
            connection.send(json_result)
            # except Exception:
            #     print(Exception.message)

        finally:
            connection.close()

if __name__ == '__main__':
    main()