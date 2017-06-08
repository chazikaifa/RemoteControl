# _*_ coding: utf-8 _*_
import pyautogui
import threading
import time
from PIL.GimpGradientFile import linear
pyautogui.FAILSAFE = False

def change_element(key,nothing):
    key = key.split('~') 
    key_1 = key[::2]
    key_2 = key[1::2]
    key = zip(key_1,key_2)
    
    x,y = pyautogui.position()

    for i in key:
        if i[0] == 'mouse':                     
            if i[1] == 'left':                  #鼠标左键点击
                pyautogui.click(x, y)
            elif i[1] == 'right':               #鼠标右键点击
                pyautogui.rightClick(x, y)
            else:                               #鼠标绝对值移动
                coords = i[1].split(',')        
                temp_x = float(coords[0])
                temp_y = float(coords[1])
                if pyautogui.onScreen(temp_x,temp_y):
                    pyautogui.moveTo(temp_x,temp_y)
        if i[0] == 'mouse@':                    #鼠标相对值移动
                coords = i[1].split(',')        
                delta_x = float(coords[0])
                delta_y = float(coords[1])
                temp_x,temp_y = boundary_determination(delta_x, delta_y)
                pyautogui.moveTo(temp_x,temp_y)       
        elif i[0] == 'mouseHold':               #鼠标按住
            pyautogui.mouseDown(x, y, i[1])
        elif i[0] == 'mouseRelease':            #鼠标松开
            pyautogui.mouseUp(x, y, i[1])
        elif i[0] == 'mouseDoubleClick':        #双击
            pyautogui.click(x,y,2,0.0,i[1],0.0,linear,None,True)
        elif i[0] == 'wheel':                   #滚轮
            clicks = int(i[1])            
            pyautogui.scroll(clicks, x, y)
        elif i[0] == 'key':                     #键盘按键
            if i[1] == '/s':
                pyautogui.press('~')
            else:
                pyautogui.press(i[1])
        elif i[0] == 'keyDown':                 #按键按下
            if i[1] == '/s':
                pyautogui.keyDown('~')
            else:
                pyautogui.keyDown(i[1])
        elif i[0] == 'keyUp':                   #按键松开
            if i[1] == '/s':
                pyautogui.keyUp('~')
            else:
                pyautogui.keyUp(i[1])
        elif i[0] == 'delay':
            sleep_time = float(i[1])/1000
            time.sleep(sleep_time)
        elif i[0] == 'string':                  #字符串输入
            print i[1]
            temp_str = i[1]
            temp_str = temp_str.replace('/s','~')   #'~'转义
            temp_str = temp_str.replace('//','/')    
            pyautogui.typewrite(temp_str)
        else:
            None

def key_to_perform(data):
    nt = 0
    if 'delay' in data: 
        th_delay = threading.Thread(target=change_element,args=(data,nt))
        th_delay.setDaemon(True)                  
        th_delay.start()
    else:
        change_element(data,nt)
        

def boundary_determination(self,delta_x,delta_y):    #防鼠标越界
    x,y = pyautogui.position()
    max_x,max_y = pyautogui.size()
    if x+delta_x <= 0:
        final_x = 0
    elif x+delta_x >= max_x:
        final_x = max_x
    else:
        final_x = x+delta_x
    
    if y+delta_y <= 0:
        final_y = 0
    elif y+delta_y >= max_y:
        final_y = max_y
    else:
        final_y = y+delta_y
        
    return final_x,final_y


      