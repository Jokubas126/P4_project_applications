import serial
import matplotlib.pyplot as plt
import time

moment = time.strftime("%Y-%b-%d__%Hh%Mm%Ss",time.localtime())

rawdata = []
count = 0
fileName = 'data_' + moment +'.txt'
#the time that the program will be running in seconds 16 min ish
timeOut = 10
#connect to the arduino
try:
	ard = serial.Serial('COM4', baudrate = 9600, timeout = 1)
except:
	print('Serial not found!')

#get a list of data
while count < timeOut:
	arduinoData = ard.readline()
	rawdata.append(str(arduinoData))
	count += 1

#clean the data from "b'xxx;xx\n\r" tp "xxx;xx"
def clean(list):
	newList = []
	for i in range(len(list)):
		#starting from the third element in the string
		temp=list[i][2:] 
		#ending at the last fifth string from the end of the string
		newList.append(temp[:-5]) 
	return newList

cleandata = clean(rawdata)

#writing the data from the list to the file
def write(list):
	file = open(fileName, 'w')
	for i in range(len(list)):
		file.write(list[i] + '\n')
	file.close()

write(cleandata)

#read the data from the file
def read():
	newList = []
	with open(fileName, 'r') as file:
		newList = file.readlines()
	#to remove the '\n'
	newList = [line.strip() for line in newList]
	file.close()
	return newList

def twoLists(list):
	toFind = ';'
	newDataList = []
	newTimeList = []
	for i in range(len(list)):
		temp = list[i]
		numb = temp.find(toFind)
		newDataList.append(temp[:-(numb-1)])
		newTimeList.append(temp[(1 + numb):])

	plt.plot(newTimeList,newDataList)
	plt.xlabel('Seconds')
	plt.ylabel('Conductivity')
	plt.title('GSR plot')
	plt.show()

	return newDataList, newTimeList

twoLists(read())