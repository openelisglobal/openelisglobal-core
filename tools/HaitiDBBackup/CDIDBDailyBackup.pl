
use File::Copy;
use File::stat;
use Cwd;

sub getTimeStamp {
	( $sec, $min, $hour, $mday, $mon, $year, $wday, $yday, $isdst ) =  localtime time ;
	my $fullYear  = $year + 1900;
	my $fullMonth = $mon + 1;

	return  "_" . $mday . "_" . $fullMonth . "_" . $fullYear . "_" . $hour . "_" . $min;
}

sub deleteOverAgedBackups {
	my $maxTimeSpan    = shift;
	my $cumulativeDir = shift;

	chdir $cumulativeDir;

	@$files = <*>;
	foreach my $file (@$files) {
		if ( time - $maxTimeSpan > stat($file)->mtime ) {
			unlink($file);
		}
	}
}

sub sendOffsite{
	my $queueDir = shift;
	my $upLoadtargetURL    = shift;
	my $upLoadUserName = shift;
	my $upLoadPassword = shift;
	
	my $maxRetryCount = 16;
	my $curlExe = 'C:\curl\curl.exe';

	chdir "$queueDir";

	my @files = <$queueDir/*.backup>; 

	foreach $file (@files) {
   		my $command = $curlExe . ' -k --user ' . $upLoadUserName . ':' . $upLoadPassword
   					. ' --url ' . $upLoadtargetURL 
   					. ' --form "dataFileName=@' . $file;
        my $command = $curlExe . ' -T ' . $file . ' ' .$upLoadUserName . ':' . $upLoadPassword . ' ' . $upLoadtargetURL
  		my $retryCount = 0;
   		my $sendSuccess = 0; #false
           
   		while ($retryCount < $maxRetryCount) {
   			my $curlReturn = `$command`;
        	my $returnStatus = $?;
        	
        	if (($returnStatus != 0) || ($curlReturn ne 'success')) {
        		print "Curl had an error. Curl said \n$curlReturn\n"
        				. "Return status $returnStatus\n";
        		$retryCount = $retryCount + 1;
        	} else {
        		$sendSuccess = 1; #true
        		last;
        	}
        	
        	sleep 7;
		}
           
		if ($sendSuccess) {
    		#remove file from system
    		unlink( $file );
    	}
	}           
}

my $postgres_pwd  = 'clinlims';
my $postgres_base = 'C:\\"Program Files"\\Postgres\8.4\\';
my $keepFileDays  = 30;
my $siteId = 'setSiteID';
my $upLoadtargetURL = 'https://sodium.cirg.washington.edu/paul-test/receive-file.pl';
my $upLoadUserName = 'test';
my $upLoadPassword = 'ied1poh2Ku';

my $snapShotFileBase     = 'lastSnapshot_' . $siteId; 
my $snapShotFileName     = $snapShotFileBase . '.backup'; 
my $cmd = 'bin\pg_dump.exe -h localhost -U clinlims >' . $snapShotFileName;

my $backBaseDir          = cwd();
my $baseFileName         = 'CDIOpenElisDB';
my $dailyDir             = "$backBaseDir\\daily";
my $cumulativeDir        = "$backBaseDir\\cumulative";
my $queueDir             = "$backBaseDir\\transmissionQueue";
my $timeStamp            = getTimeStamp();
my $todaysCummlativeFile = "$baseFileName$siteId$timeStamp.backup";
my $maxTimeSpan = 60 * 60 * 24 * $keepFileDays;

$ENV{'PGPASSWORD'} = "$postgres_pwd";

chdir "$dailyDir";
my $response = system("$postgres_base$cmd")  and warn "Error while running: $! \n";

copy( $snapShotFileName, "$cumulativeDir/$todaysCummlativeFile" ) or die "File cannot be copied.";
copy( $snapShotFileName, "$queueDir/$todaysCummlativeFile" ) or die "File cannot be copied.";

deleteOverAgedBackups ($maxTimeSpan, $cumulativeDir);

#sendOffsite($queueDir, $upLoadtargetURL, $upLoadUserName, $upLoadPassword);

   









