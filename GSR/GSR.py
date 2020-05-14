import serial
import matplotlib.pyplot as plt
import time
import statistics

moment = time.strftime("%Y-%b-%d__%Hh%Mm%Ss",time.localtime())

rawdata = []
count = 0
fileName = 'data_' + moment +'.txt'
#the time that the program will be running in seconds 16 min ish
timeOut = 120
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

#plot the data
def twoLists(list):
	toFind = ';'
	newDataList = []
	newTimeList = []
	for i in range(len(list)):
		temp = list[i]
		numb = temp.find(toFind)
		if i < 10:
			newDataList.append(temp[:-(numb - 1)])
		if i >= 10 and i < 100:
			newDataList.append(temp[:-(numb)])
		if i >= 100 and i < 1000:
			newDataList.append(temp[:-(numb + 1)])
		newTimeList.append(temp[(1 + numb):])

	return newDataList, newTimeList

rawdatalist, timelist = twoLists(read())

#applying a mean and median filter for each i+4, i-4 data range
#and using it to calculate the phasic data
def filteredList(list):
	filteredList = []
	kernItems = []
	for i in range(len(list)):
		if i > 4 and i < (len(list) - 4):
			for j in range(i - 4, i + 4):
				kernItems.append(int(list[j]))
			medianValue = np.median(kernItems)
			meanValue = np.mean(kernItems)
			#phasic data
			filteredList.append(medianValue - meanValue)


	print(filteredList)
	return filteredList

#ploting the data
def plot(datalist, timelist):
	plt.plot(timelist,datalist)
	plt.xlabel('Seconds')
	plt.ylabel('Conductivity')
	plt.title('GSR plot')
	plt.show()

plot(rawdatalist,timelist)
plot(filteredList(rawdatalist), timelist[5:len(timelist)-4])