# _*_ coding: utf-8 _*_
import socket
import threading
import time 
import control
import outsave


class Socketserver():      
    def Server_start(self,addr=None,port=9999):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.server.bind((addr, port))
        self.server.listen(5)
        self.custm = {}
        print 'waiting'
        outsave.OUTSAVE = '端口绑定成功 等待连接请求\n'
        outsave.FLAG = True
        while True:
            sock,c_addr = self.server.accept()
            print '%s '% c_addr[0]
            outsave.OUTSAVE = '%s 已连接\n'% c_addr[0]
            outsave.FLAG = True
            self.custm[c_addr[0]] = 'True'
            print self.custm
            t = threading.Thread(target=self.Link_start,args=(sock,c_addr))
            t.setDaemon(True)
            t.start()
    def return_ms(self):
        return self.message    
    def Link_start(self,sock,addr):     
        while True:
            if self.custm[addr[0]] == 'False':
                break
            data = sock.recv(1024)
#             time.sleep(1)
            L = data.split("\n")[0]
            print L
            outsave.OUTSAVE = L+'\n'
            outsave.FLAG = True
            control.key_to_perform(L)
        del(self.custm[addr[0]])
        outsave.OUTSAVE = '%s 已断开连接\n'% addr[0]
        outsave.FLAG = True
        sock.close() 


    
          