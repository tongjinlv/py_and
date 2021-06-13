import sys
import math
import random
import time
import imp


class timer:
    def __init__(self,call):
        self.call=call
    def sleep(self,t):
        if isinstance(t,int):
            for num in range(0,t,1):
                time.sleep(1)
        elif isinstance(t,float):
            time.sleep(t*1.13)
    def sleep_unit_time(self,t):
        t_=0
        if isinstance(t,int):
            t_=t
        elif isinstance(t,float):
            t_=round(t)
        elif isinstance(t,str):
            if(t=="random"):
                t_=random.randint(1,6)
            else:
                t_=int(t) 
        for num in range(0,t_,1):
            time.sleep(0.88)
        
        