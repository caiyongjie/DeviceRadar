#! /usr/bin/python
import sys, os
import numpy as np
import matplotlib.pyplot as plt

def read_azimuth_ss(fname):
        data = {}
        try:
                for line in open(fname):
                        slist = line.split(',')
                        ap = slist[3]
                        ssid = slist[2]
                        if ssid == "rpi_wpa2":
                                if ap in data:
                                        data[ap].append(map(float,[slist[0],slist[1],slist[4]]))
                                else:
                                        data[ap] = [map(float,[slist[0],slist[1],slist[4]])]
        except:
                print "read file %s error!"%fname
        draw = 0
        if draw:
                c = "rbgmkyrbgmkyrbgmky"
                markers = "o^D+x*12348"
                fig = plt.figure()
                i = 0
                for ap in data:
                        plt.scatter([x[1] for x in data[ap]],[x[2] for x in data[ap]],3,c=c[i],marker=markers[i])
                        i+=1
        #plt.show()
        return data

def read_combine_azimuth_ss(fname):
	data = read_azimuth_ss(fname)
	cluster = {}
	for ap in data:
		cluster[ap] = []
		preRecordList = []
		for record in data[ap]:
			t = record[0]
			orientation = record[1]
			ss = record[2]
			preLen = len(preRecordList)
			if preLen >0:
				pt = preRecordList[preLen-1][0]
				po = preRecordList[preLen-1][1]
				if abs(pt - t) < 5*1000 and abs(po - orientation)<3:
					preRecordList.append(record)	
				else:
					sump = map(sum, [[x[0] for x in preRecordList],[x[1] for x in preRecordList],[x[2] for x in preRecordList]])
					
					cluster[ap].append([x/preLen for x in sump])
					preRecordList = [record]
			else:
				preRecordList = [record]			
	draw = 0
	if draw:	
		c = "rbgmkyrbgmkyrbgmky"
		markers = "o^D+x*12348"
		fig = plt.figure()
		i = 0
		for ap in data:
			plt.scatter([x[1] for x in data[ap]],[x[2] for x in data[ap]],3,c=c[i],marker=markers[i])
			i+=1
	#plt.show()
	return cluster

#smooth: hat(SS_t) = alfa * hat(SS_t-1) + (1-alfa) * SS_t
def read_combine_smooth_azimuth_ss(fname):
	cluster = read_combine_azimuth_ss(fname)
		

def draw():
	c = "yrbgmk"
	markers = "o^D+x*12348"
	sw = 15

#data1 = read_aziumth_ss("../data/Wifum_ap_20140314171717.xml") 	
#data2 = read_aziumth_ss("../data/Wifum_ap_20140314172014.xml")
#data3 = read_aziumth_ss("../data/Wifum_ap_20140314172806.xml")

	allfname = ["../data/Wifum_ap_20140319150355.xml","../data/Wifum_ap_20140319150712.xml","../data/Wifum_ap_20140319151243.xml","../data/Wifum_ap_20140319171037.xml"]
	flist = [allfname[0],allfname[2],allfname[3]]
	data = []
	for fname in flist:
		data.append(read_azimuth_ss(fname))

	s = map(set,[x.keys() for x in data])
	folsomF4_ap = ["58:35:d9:65:6b:61","58:35:d9:65:7a:41","58:35:d9:65:5f:f1"]
	for ap in folsomF4_ap:
		plt.figure()
		i = 0
		for p in data:
			if ap in p:
				plt.scatter([x[1] for x in p[ap]],[x[2] for x in p[ap]],sw,c=c[i],marker=markers[i])
        		i = i+1
	dcluster = []
	for fname in flist:
		dcluster.append(read_combine_azimuth_ss(fname))
	for ap in folsomF4_ap:
                plt.figure()
                i = 0
                for p in dcluster:
                        if ap in p:
                                plt.scatter([x[1] for x in p[ap]],[x[2] for x in p[ap]],sw,c=c[i],marker=markers[i])
                        i = i+1
		 
	plt.show()
