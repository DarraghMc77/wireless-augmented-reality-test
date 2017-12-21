import sys
import socket
from darkflow.net.build import TFNet
import cv2
import json

CONN_QUEUE = 50

def main():
    options = {"model": "cfg/yolo.cfg", "load": "bin/yolo.weights", "threshold": 0.1}

    tfnet = TFNet(options)

    host = ''
    port = 8011
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
            txt = data.decode('utf-8')
            amount_received = 0

            # Get size of image byte[]
            if txt.startswith("SIZE"):
                size = int(txt.split()[1])
                print(size)
            while True:
                print(amount_received)
                data = connection.recv(4096)
                amount_received += len(data)
                img += data
                if(amount_received >= size):
                    break
            print("Image received")
            fh = open("./testImage.jpg", "wb")
            fh.write(img)
            fh.close()

            # Run YOLO prediction
            imgcv = cv2.imread("./testImage.jpg")
            result = tfnet.return_predict(imgcv)

            for obj in result:
                obj['confidence'] = str(obj['confidence'])

            json_result = json.dumps(result)
            print(json_result)
            connection.send(json_result)

        finally:
            connection.close()

if __name__ == '__main__':
    main()