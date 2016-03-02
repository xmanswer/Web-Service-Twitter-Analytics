sudo apt-get update
sudo apt-get install python-pip
sudo pip install awscli
aws configure
## AWSAccessKeyId=AKIAJWVJUGINPZKLNC5A
## AWSSecretKey=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
## default region = us-east-1
## default format = txt
aws s3 ls s3://cmucc-datasets/twitter/f15/
sudo apt-get install s3cmd
s3cmd --configure
# then configure s3cmd
Access Key: AKIAJWVJUGINPZKLNC5A
Secret Key: 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
Encryption password: hmm
Path to GPG program [/usr/bin/gpg]: /usr/bin/gpg
Use HTTPS protocol [No]: False
HTTP Proxy server name:
Test access with supplied credentials? [Y/n] y
Save settings? [y/N] y


s3cmd cp s3://cmucc-datasets/twitter/f15/part-00000 s3://phase1elt/input/

export CLASSPATH=$CLASSPATH:~/Downloads/gson-2.4-javadoc.jar:~/Downloads/gson-2.4-sources.jar:~/Downloads/gson-2.4.jar

cat subset3.txt | java -Duser.timezone=GMT ETLMapperQuery3Step1 afinn.txt banned.txt | sort | java -Duser.timezone=GMT ETLReducerQuery3Step1
export CLASSPATH=$CLASSPATH:~/etl/gson-2.4-javadoc.jar:~/etl/gson-2.4-sources.jar:~/etl/gson-2.4.jar
javac *.java
jar -cvf ETL.jar *.class
cat subset3.txt | java ETLMapperPhase1 "/home/ubuntu/etl/afinn.txt" "/home/ubuntu/etl/banned.txt"

mkdir phase1_etl
aws s3 cp s3://cmucc-datasets/twitter/f15/part-00000 ~/phase1_etl
head -1 ~/phase1_etl/part-00000


# copy a subset to local file, and decoding
head -1000 part-00000 > subset1000.txt
scp -i hmm.pem ubuntu@ec2-54-86-101-207.compute-1.amazonaws.com:/home/ubuntu/phase1_etl/subset1000.txt ~/Downloads



2015-10-14 01:33:07 1765057690 part-00000 to part-00661, around 15G per part



hadoop-streaming -files s3://phase1elt/src/ETL.jar,s3://phase1elt/src/gson-2.4.jar,s3://phase1elt/src/gson-2.4-javadoc.jar,s3://phase1elt/src/gson-2.4-sources.jar,s3://phase1elt/src/afinn.txt,s3://phase1elt/src/banned.txt -mapper "java -classpath ETL.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLMapperPhase1 afinn.txt banned.txt" -reducer "java -classpath ETL.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLReducerPhase1" -input s3://phase1elt/input/ -output s3://phase1elt/output/ -cmdenv LC_CTYPE=en_GB.UTF-8


hadoop-streaming -files s3://phase1elt/src/ETL.jar,s3://phase1elt/src/gson-2.4.jar,s3://phase1elt/src/gson-2.4-javadoc.jar,s3://phase1elt/src/gson-2.4-sources.jar,s3://phase1elt/src/afinn.txt,s3://phase1elt/src/banned.txt -mapper "java -classpath ETL.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLMapperPhase1 afinn.txt banned.txt" -reducer "java -classpath ETL.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLReducerPhase1" -input s3://phase1elt/input/ -output s3://phase1elt/output/ -cmdenv LC_CTYPE=en_GB.UTF-8




hadoop-streaming -files s3://phase1elt/src/ETL.jar,s3://phase1elt/src/gson-2.4.jar,s3://phase1elt/src/gson-2.4-javadoc.jar,s3://phase1elt/src/gson-2.4-sources.jar,s3://phase1elt/src/afinn.txt,s3://phase1elt/src/banned.txt -mapper "java -classpath ETL.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLMapperPhase1 afinn.txt banned.txt" -reducer "java -classpath ETL.jar:gson-2.4.jar:gson-2.4-javadoc.jar:gson-2.4-sources.jar ETLReducerPhase1" -input s3://phase1elt/input/ -output s3://phase1elt/output/ -cmdenv LC_CTYPE=en_GB.UTF-8