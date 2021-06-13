import sys
import math
import random
import imp
import time
from controller.func  import func


class sensor:
    def __init__(self,call):
        self.call=call
    def wait_until(self,func):
        if(func==None):
            self.call.alert("语法错误")
            return
        if type(func) == type(True):
            if(func):
                return
            else:
                while True:
                    time.sleep(0.001)
        else:
            while(True):
                r=func()
                if(r!=None):
                    if(r==True):
                        return
                else:
                    time.sleep(0.001)
                pass
    def get_rgb_value(self,color):
        data = [0x28,0x02,0x01]
        if(color=="red"):
            data[2]=0x01
        elif(color=="green"):
            data[2]=0x02
        elif(color=="blue"):
            data[2]=0x03
        self.call.blewrite(data)
        r=self.call.blewait(0x28)
        if(r==None):
            return 0
        else:
            return r[4]
