import matplotlib.pyplot as plt

#read the data from the file
def read():
	newList = []
	with open('test.txt', 'r') as file:
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

def plotingGSR(list):
	newList = []
	medianList = []
	for i in range(len(list)):
		