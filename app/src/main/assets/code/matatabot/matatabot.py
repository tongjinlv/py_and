import sys
import math
import random
import imp
from java import jclass

from matatabot.motion import motion
from matatabot.music import music
from matatabot.emotion import emotion
from matatabot.speaker import speaker
from matatabot.leds import leds
imp.load_source('matatabotmotion', '/data/data/com.matatalab.matatacode/run/matatabot/motion.py')
imp.load_source('matatabotmusic', '/data/data/com.matatalab.matatacode/run/matatabot/music.py')
imp.load_source('matatabotemotion', '/data/data/com.matatalab.matatacode/run/matatabot/emotion.py')
imp.load_source('matatabotspeaker', '/data/data/com.matatalab.matatacode/run/matatabot/speaker.py')
imp.load_source('matatabotleds', '/data/data/com.matatalab.matatacode/run/matatabot/leds.py')

class matatabot:
    call=None
    music=None
    motion=None
    emotion=None
    speaker=None
    leds=None
    def __init__(self):
        Python2Java = jclass("com.matatalab.matatacode.model.Python2Java")
        self.call = Python2Java("python")
        self.music=music(self.call)
        self.motion=motion(self.call)
        self.emotion=emotion(self.call)
        self.speaker=speaker(self.call)
        self.leds=leds(self.call)
       # data = [0x7e,0x02,0x02,0x00,0x00]
       # print("小车 设置为新协议")
       # self.call.blewrite(data)
       # self.call.blewait()
    def start(self):
        data = [0x85]
        self.call.blewrite(data)
        self.call.blewait()

