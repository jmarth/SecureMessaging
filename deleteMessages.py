import pymysql
import time

db = pymysql.connect(host="easel2.fulgentcorp.com",    # your host, usually localhost
                     user="oqa474",         # your username
                     passwd="37awlWr0jm4Kxi67n5SI",  # your password
                     db="oqa474")        # name of the data base

cur = db.cursor()

while(True):
	#cur.execute("SET time_zone = '-4:00';DELETE FROM messages WHERE NOW() > timeoutDateTime;")
	cur.execute("SET time_zone = '-4:00';")
	cur.execute("SELECT NOW();")
	row = cur.fetchone()
	now = row[0]

	print("Looking for messages")

	cur.execute("SELECT * from messages")
	row = cur.fetchone()

	while row:
	    id = row[0]
	    timeoutDateTime = row[4]
	    if(now > timeoutDateTime):
	    	print("found expired message")
	    	cur.execute("DELETE FROM messages WHERE id = %s", id)
	    	print("****************************************************************************************")
	    	print("message deleted")
	    	print("****************************************************************************************")
	    	db.commit()
	    else:
	    	print("message found but has not expired")

	    row = cur.fetchone()

	db.commit()
	time.sleep(1)