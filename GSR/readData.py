import matplotlib.pyplot as plt
import statistics
import numpy as np

#read the data from the file
def read():
	newList = []
	with open('datawhilewatching.txt', 'r') as file:
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
		newDataList.append(1000 - int(temp[:(numb)]))
		newTimeList.append(int(temp[(1 + numb):]))
	return newDataList, newTimeList

rawdatalist, timelist = twoLists(read())

#applying a mean and median filter for each i+4, i-4 data range
#and using it to calculate the phasic data
def filteredList(list):
	filteredList = []
	for i in range(len(list)):
		kernItems = []
		if i > 4 and i < (len(list) - 4):
			for j in range(i - 4, i + 4):
				kernItems.append(int(list[j]))
			medianValue = np.median(kernItems)
			#phasic data
			filteredList.append(list[i] - medianValue)


	print(filteredList)
	return filteredList

#ploting the data
def plot(datalist, timelist, other, secondother):
	fig,a =  plt.subplots(2)

	a[0].plot(timelist,datalist)
	a[1].plot(secondother, other)
	plt.show()

	# plt.plot(timelist,datalist)
	# plt.xlabel('Seconds')
	# plt.ylabel('Conductivity')
	# plt.title('GSR plot')
	# plt.show()



plot(filteredList(rawdatalist), timelist[5:len(timelist)-4], rawdatalist, timelist)