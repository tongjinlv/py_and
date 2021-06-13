# -*- coding: utf-8 -*-
from java import jclass
import sys
import struct
Python2Java = jclass("com.matatalab.matatacode.model.Python2Java")
android = Python2Java("python")
sys.stdout=android

#################################################################
import math
import time
import imp
import threading

from matatabot.matatabot import matatabot
from controller.controller import controller
from controller.func import func


imp.load_source('controller', '/data/data/com.matatalab.matatacode/run/controller/controller.py')
imp.load_source('matatabot', '/data/data/com.matatalab.matatacode/run/matatabot/matatabot.py')
print("-----------------main start------------------------")

controller  = controller()
controller.message.wait("1")

print("--------------------end--------------------------")

