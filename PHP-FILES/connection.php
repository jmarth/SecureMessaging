<?php

	require_once 'config.php';
	
	class DB_Connection {
		
		private $connect;
		function __construct() {
			$this->connect = mysqli_connect(hostname, user, password, db_name)
			or die("Could not connect to db");

			if (!$this->connect->set_charset('utf8')) {
	    		#printf("Error loading character set utf8: %s\n", $this->connect->error);
	    		exit;
			}
			
		}
		
		public function getConnection()
		{
			return $this->connect;
		}
	}

?>