from subprocess import call
pre = "s3://phase1elt/outputOneTenth_v2/part-"
l = []
for num in range(0, 19):
	l.append(pre + '{0:05}'.format(num))

for str in l:
	call(["hadoop", "fs", "-copyToLocal", str])
