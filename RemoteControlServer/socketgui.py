# _*_ coding: utf-8	_*_
import Tkinter
import localip
import echoserver
import threading
import outsave
import sys
from ScrolledText import ScrolledText


class Socketgui(Tkinter.Tk):
	def __init__(self):
		Tkinter.Tk.__init__(self)
		self.title('RemoteControlServer')
		self.geometry('300x200')
		self.server = echoserver.Socketserver()
		self.set_widgets()
		self.resizable(width=False, height=False)
		
		
	def set_widgets(self):
		self.port_label = Tkinter.Label(self,text='端口号 :')
		self.port_label.place(x=30,y=25)
		self.default_port = Tkinter.StringVar()
		self.port_input = Tkinter.Entry(self,width=18,textvariable=self.default_port)
		self.default_port.set('4444')
		self.port_input.place(x=80,y=25)
		self.port_label = Tkinter.Label(self,text='IP地址 :')
		self.port_label.place(x=30,y=60)
		self.ip_readonly = Tkinter.StringVar()
		self.ip_display = Tkinter.Entry(self,text=self.ip_readonly,width=18)
		self.ip_readonly.set(localip.get_local_ip())
		self.ip_display['state'] = 'readonly'
		self.ip_display.place(x=80,y=60)
		self.start_btn = Tkinter.Button(self,text='开启',command=self.start_btn_response)
		self.start_btn.place(x=220,y=21)
		self.stop_btn = Tkinter.Button(self,text='清空',command=self.clear_btn_response)
		self.stop_btn.place(x=220,y=56)
		
		
# 		self.ms_readonly = Tkinter.StringVar()
# 		self.ms_display = Tkinter.Entry(self,textvariable=self.ms_readonly,width=25,relief='flat',highlightcolor='white')
# 		self.ms_readonly.set(outsave.OUTSAVE)
# # 		self.ms_display['state'] = 'readonly'
# 		self.ms_display.place(x=30,y=120)
# 		self.last_ms = outsave.OUTSAVE
# 		self.th_outsave = threading.Thread(target=self.output_change)
# 		self.th_outsave.setDaemon(True)
# 		self.th_outsave.start()
		
		self.ms_listbox = ScrolledText(self,height=6,width=35,relief='flat')
		self.ms_listbox.place(x=5,y=100)
		self.th_outsave = threading.Thread(target=self.ms_display)
		self.th_outsave.setDaemon(True)
		self.th_outsave.start()
		
		
	def start_btn_response(self):
		self.port = int(self.port_input.get())
		self.addr = localip.get_local_ip()
		self.server_thread = threading.Thread(target=self.server.Server_start,args=(self.addr,self.port))
		self.server_thread.setDaemon(True)				#子线程随主线程一起关闭	
		self.server_thread.start()
	def clear_btn_response(self):
		self.ms_listbox.delete(1.0,'end')
	def ask_quit(self):
		print "i'm here"
		self.quit()

	def ms_display(self):
		while True:
			if outsave.FLAG:
				self.ms_listbox.insert('end', outsave.OUTSAVE)
				outsave.FLAG = False
				self.ms_listbox.yview_scroll(1, "units")
# 	def output_change(self):
# 		while True:
# 			if self.last_ms != outsave.OUTSAVE:
# 				self.last_ms = outsave.OUTSAVE
# 				self.ms_readonly.set(self.last_ms)
		
		
if __name__ == '__main__':
	mygui = Socketgui()
	mygui.protocol("WM_DELETE_WINDOW",mygui.ask_quit)
	mygui.mainloop()
	
