import sys
import math
import random
import imp
import struct

class func:
    f=None
    v=None
    def __init__(self,func,value):
        self.f=func
        self.v=value
        print("xxxxx:"+self.v)
    def func(self):
        return self.f
    def value(self):
        return self.v