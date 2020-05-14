import matplotlib.pyplot as plt
import statistics
import numpy as np

#read the data from the file
def read():
	newList = []
	with open('data_2020-May-13__21h28m38s.txt', 'r') as file:
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

plot(filteredList(rawdatalist), timelist[5:len(timelist)-4])