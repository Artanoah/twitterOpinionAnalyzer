install.packages("RTextTools")
library(rjson)
library(RTextTools)
library(data.table)

setwd("/Users/kbrusch/Google Drive/class/WS_2014/LA/twitterOpinionAnalyzer")
d <- as.data.table(read.csv("alleBewertet2.txt", header=TRUE))
str(d)

doc_matrix <- create_matrix(d$text, language="english", removeNumbers=TRUE,
                            stemWords=TRUE, removeSparseTerms=.998)

container <- create_container(doc_matrix, d$rating, trainSize=1:200,
                              testSize=201:260, virgin=FALSE)


################################################
##### RANDOM FOREST
################################################
RF <- train_model(container,"RF")
RF_CLASSIFY <- classify_model(container, RF)
#RF_CLASSIFY <- as.data.table(RF_CLASSIFY)
create_analytics(container,RF_CLASSIFY)


################################################
##### SUPPORT VECTOR MACHINE
################################################
SVM <- train_model(container,"SVM")
SVM_CLASSIFY <- classify_model(container, SVM)
#NNET_CLASSIFY <- as.data.table(NNET_CLASSIFY)
create_analytics(container,SVM_CLASSIFY)


################################################
##### NEURAL NET
################################################
NNET <- train_model(container,"NNET")
NNET_CLASSIFY <- classify_model(container, NNET)
#NNET_CLASSIFY <- as.data.table(NNET_CLASSIFY)
create_analytics(container,NNET)


