sudo apt-get update
sudo apt-get install python-pip

# configue cli
sudo pip install awscli
aws configure
## AWSAccessKeyId=AKIAJWVJUGINPZKLNC5A
## AWSSecretKey=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
## default region = us-east-1
## default format = txt

# copy subsample
aws s3 ls s3://cmucc-datasets/twitter/f15/
sudo apt-get install s3cmd
s3cmd --configure
# configure s3cmd
## Access Key: AKIAJWVJUGINPZKLNC5A
## Secret Key: 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
## Encryption password: hmm
## Path to GPG program [/usr/bin/gpg]: /usr/bin/gpg
## Use HTTPS protocol [No]: False
## HTTP Proxy server name:
## Test access with supplied credentials? [Y/n] y
## Save settings? [y/N] y
s3cmd cp s3://cmucc-datasets/twitter/f15/part-00000 s3://phase1elt/input/

# prepare emr
cd ~/
mkdir etl
sudo apt-get install openjdk-7-jdk
# scp mapper and reducer source code, gson jars, the afinn and banned txt, and a tiny sample subset3.txt to etl
export CLASSPATH=$CLASSPATH:~/etl/gson-2.4-javadoc.jar:~/etl/gson-2.4-sources.jar:~/etl/gson-2.4.jar
javac *.java
jar -cvf ETL.jar *.class
# test mapper and reducer on the single machine
# cat subset3.txt | java ETLMapperPhase1 "/home/ubuntu/etl/afinn.txt" "/home/ubuntu/etl/banned.txt"
cat subset3.txt | java -Duser.timezone=GMT ETLMapperPhase1_v2 /home/ubuntu/etl/afinn.txt /home/ubuntu/etl/banned.txt |sort| java -Duser.timezone=GMT ETLReducerPhase1_v2

# go to emr console, customize the streaming as follows
# using command-runner.jar as JAR location
hadoop-streaming -files s3://phase1elt/src/ETL.jar,s3://phase1elt/src/gson-2.4.jar,s3://phase1elt/src/gson-2.4-javadoc.jar,s3://phase1elt/src/gson-2.4-sources.jar,s3://phase1elt/src/afinn.txt,s3://phase1elt/src/banned.txt -mapper "java -classpath ETL.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLMapperPhase1 afinn.txt banned.txt" -reducer "java -classpath ETL.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLReducerPhase1" -input s3://phase1elt/input/ -output s3://phase1elt/output/ -cmdenv LC_CTYPE=en_GB.UTF-8


hadoop-streaming -files s3://phase1elt/src/ETL_v2.jar,s3://phase1elt/src/gson-2.4.jar,s3://phase1elt/src/gson-2.4-javadoc.jar,s3://phase1elt/src/gson-2.4-sources.jar,s3://phase1elt/src/afinn.txt,s3://phase1elt/src/banned.txt -mapper "java -Duser.timezone=GMT -classpath ETL_v2.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLMapperPhase1_v2 afinn.txt banned.txt" -reducer "java -Duser.timezone=GMT -classpath ETL_v2.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLReducerPhase1_v2" -input s3://phase1elt/input/ -output s3://phase1elt/output_v2/ -cmdenv LC_CTYPE=en_GB.UTF-8


hadoop-streaming -files s3://phase1elt/src/ETL_v3.jar,s3://phase1elt/src/gson-2.4.jar,s3://phase1elt/src/gson-2.4-javadoc.jar,s3://phase1elt/src/gson-2.4-sources.jar,s3://phase1elt/src/afinn.txt,s3://phase1elt/src/banned.txt -mapper "java -Duser.timezone=GMT -classpath ETL_v3.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLMapperPhase1_v3 afinn.txt banned.txt" -reducer "java -Duser.timezone=GMT -classpath ETL_v3.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLReducerPhase1_v3" -input s3://cmucc-datasets/twitter/f15/ -output s3://phase1elt/output_full_v3/ -cmdenv LC_CTYPE=en_GB.UTF-8