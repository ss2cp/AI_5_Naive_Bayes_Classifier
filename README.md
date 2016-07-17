# AI_HW5
a classification program with Naive Bayes Classifier

## Background
This is a classification program implemented with Naive Bayes Classifier. 

**Training Data:** A training set of 1500 census data. Each line of data has 13 features, 5 of which are continuous. The response variables are the salaries of citizens, either >50K or <=50K, based on various features in the data.

####*Screenshot of Sample Data*
![data sample](https://raw.githubusercontent.com/ss2cp/AI_HW5/master/results/data_sample.png)

**Goal:** To predict the results of another set of data, given all 13 features of each data. 

**Language:** Java

## Naive Bayes Classifier
*Naive Bayes Classifier* uses a simple 2-layer Bayesian Network (1 root node with a layer of leaf nodes). 

The *learning process* pre-calculates all the probabilities of each feature given a response, which will later be applied, in the *predict process*, to calculate the probabilities of each response given some features.

There are 2 kinds of data in the training set, continuous and discrete (categorical). The approaches to these two cases are slightly different, but the overall ideas are similar. 

For *continuous case*, we need to pre-calculate means and variances for each feature. Then we use the *probability density function* showing below to calculate the probabilities, plugging in same *Vs* for both responses, and choose the higher response as our final prediction.

<img src="https://raw.githubusercontent.com/ss2cp/AI_HW5/master/results/probability_density_function.png" width="250">

For *discrete case*, we need to count occurences of each feature given each response, and calculate the
![alt tag](https://raw.githubusercontent.com/ss2cp/AI_HW5/master/results/Discrete.png)

![alt tag](https://raw.githubusercontent.com/ss2cp/AI_HW5/master/results/Both.png)
