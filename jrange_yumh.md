# range  
   
`jrange` operates ranges and links of ranges on chromosomes.

`jrange`可操作染色体范围


## Compile
编译

		brew install wang-q/tap/jrange
利用homebrew下载安装jrange
		sudo cpanm App::Rangeops
利用cpanm下载安装App::Rangeops

## Example
例子

```bash
Usage: <main class> [options] [command] [command options]
用法：<主体> [选项] [命令] [命令选项]

		Options:
		选项：
				--help  -h
			Print this help and quit
			Default: false
    
    Commands:
    命令：
```

### 1. circos

Range links to circos links or highlight file

It's assumed that all ranges in input files are valid

将range links转换成circos links或者高亮文件

``` bash
Usage: rangeops circos [options] <infiles> ()

		Options:
				--outfile  -o
			Output filename. [stdout] for screen.
				--remove  -r
				--highlight  -l 
			Create highlights instead of links
```

* Example:

```bash
rangeops circos t/II.connect.tsv --highlight -o stdout (App::Rangeops)
```

* Explanation:

II.connect.tsv：
    
```bash
II(+):1990-5850	VI(+):892-4685	XII(+):7326-11200
II(+):810776-812328	XIII(-):6395-7947	XIV(-):7479-9033
II(+):1-2018	XII(+):204-2215
II(+):300165-301260	IV(+):471852-472948
II(+):477671-479048	XVI(+):700594-701971
II(+):804880-813096	VII(+):1076129-1084340
```

经过`rangeops circos t/II.connect.tsv --highlight -o stdout`(App::Rangeops)后可以得到:

```bash
II 1990 5850 fill_color=paired-12-qual-12
VI 892 4685 fill_color=paired-12-qual-12
XII 7326 11200 fill_color=paired-12-qual-12
II 810776 812328 fill_color=paired-12-qual-12
XIII 6395 7947 fill_color=paired-12-qual-12
XIV 7479 9033 fill_color=paired-12-qual-12
II 1 2018 fill_color=paired-12-qual-12
XII 204 2215 fill_color=paired-12-qual-12
II 300165 301260 fill_color=paired-12-qual-12
IV 471852 472948 fill_color=paired-12-qual-12
II 477671 479048 fill_color=paired-12-qual-12
XVI 700594 701971 fill_color=paired-12-qual-12
II 804880 813096 fill_color=paired-12-qual-12
VII 1076129 1084340 fill_color=paired-12-qual-12
```

### 2. clean
 
Replace ranges within links, incorporate hit strands and remove nested links

将range links中重复或者重叠的部分剔除

```bash
Usage: jrange clean [options] <infiles>

		Options:
				--bundle  -b
			Bundle overlapped links. This value is the overlapping size.
			Suggested value is [500].
			Default: 0
				--outfile  -o
			Output filename. [stdout] for screen.
				--replace  -r
			Two-column tsv file, normally produced by command merge.
				--verbose  -v
			Verbose mode.
			Default: false
```

* Example:

```bash
jrange clean t/II.sort.tsv -o stdout
jrange clean t/II.sort.tsv --bundle 500 -o stdout
jrange clean t/II.sort.tsv -r t/II.merge.tsv -o stdout
rangeops clean t/II.sort.tsv -o stdout (App::Rangeops)
rangeops clean t/II.sort.tsv --bundle 500 -o stdout (App::Rangeops)
rangeops clean t/II.sort.tsv -r t/II.merge.tsv -o stdout (App::Rangeops)
```

* Explanation:

II.sort.tsv:

```bash
II(+):1-2018	XII(+):204-2215	+
II(+):1990-5850	II(+):2026-5850	+
II(+):1990-5850	XII(+):7326-11200	+
II(+):2026-5850	VI(+):892-4684	+
II(+):2026-5850	VI(+):893-4685	+
II(+):2026-5850	XII(+):7326-11200	+
II(+):144228-145732	II(-):144228-145732	-
II(+):300165-301260	IV(+):471852-472948	+
II(+):429496-430989	II(+):429504-430965	+
II(+):477671-479048	XVI(+):700594-701971	+
II(+):658738-662234	II(-):658738-662234	-
II(+):804880-813096	VII(+):1076129-1084340	+
II(+):806179-808955	VII(+):1077427-1080204	+
II(+):810776-812328	XIII(-):6395-7947	+
II(+):810776-812328	XIV(-):7479-9033	+
```

经过`jrange clean t/II.sort.tsv -o stdout`后可以得到:

```bash
II(+):1-2018	XII(+):204-2215
II(+):1990-5850	II(+):2026-5850
II(+):1990-5850	XII(+):7326-11200
II(+):2026-5850	VI(+):892-4684
II(+):2026-5850	VI(+):893-4685
II(+):300165-301260	IV(+):471852-472948
II(+):429496-430989	II(+):429504-430965
II(+):477671-479048	XVI(+):700594-701971
II(+):804880-813096	VII(+):1076129-1084340
II(+):810776-812328	XIII(-):6395-7947
II(+):810776-812328	XIV(-):7479-9033
```
经过`jrange clean t/II.sort.tsv --bundle 500 -o stdout`后可以得到:

```bash
II(+):1-2018	XII(+):204-2215
II(+):1990-5850	II(+):2026-5850
II(+):1990-5850	XII(+):7326-11200
II(+):2026-5850	VI(+):892-4685
II(+):300165-301260	IV(+):471852-472948
II(+):429496-430989	II(+):429504-430965
II(+):477671-479048	XVI(+):700594-701971
II(+):804880-813096	VII(+):1076129-1084340
II(+):810776-812328	XIII(-):6395-7947
II(+):810776-812328	XIV(-):7479-9033
```

II.merge.tsv:

```bash
II(+):2026-5850	II(+):1990-5850
II(+):429504-430965	II(+):429496-430989
II(-):144228-145732	II(+):144228-145732
II(-):658738-662234	II(+):658738-662234
VI(+):893-4685	VI(+):892-4685
VI(+):892-4684	VI(+):892-4685
```

经过`jrange clean t/II.sort.tsv -r t/II.merge.tsv -o stdout`后可以得到:

```bash
II(+):1-2018	XII(+):204-2215
II(+):1990-5850	VI(+):892-4685
II(+):1990-5850	XII(+):7326-11200
II(+):300165-301260	IV(+):471852-472948
II(+):477671-479048	XVI(+):700594-701971
II(+):804880-813096	VII(+):1076129-1084340
II(+):810776-812328	XIII(-):6395-7947
II(+):810776-812328	XIV(-):7479-9033
```

### 3. connect      

Connect range links in paralog graph

将range links中相同的进行连接

```bash
Usage: rangeops connect [options] <infiles>
        
		Options:
				--outfile  -o
			Output filename. [stdout] for screen.
```

* Example:

```bash
rangeops connect t/II.clean.tsv -o stdout (App::Rangeops)
```

* Explanation:

II.clean.tsv:

```bash
II(+):1-2018	XII(+):204-2215
II(+):1990-5850	XII(+):7326-11200
II(+):1990-5850	VI(+):892-4685
II(+):300165-301260	IV(+):471852-472948
II(+):477671-479048	XVI(+):700594-701971
II(+):804880-813096	VII(+):1076129-1084340
II(+):810776-812328	XIII(-):6395-7947
II(+):810776-812328	XIV(-):7479-9033
```

经过`rangeops connect t/II.clean.tsv -o stdout`(App::Rangeops)后可以得到:

```bash
II(+):1990-5850	VI(+):892-4685	XII(+):7326-11200
II(+):810776-812328	XIII(-):6395-7947	XIV(-):7479-9033
II(+):1-2018	XII(+):204-2215
II(+):300165-301260	IV(+):471852-472948
II(+):477671-479048	XVI(+):700594-701971
II(+):804880-813096	VII(+):1076129-1084340
```

### 4. create

Mandatory parameter 'genome' missing in call to (eval)

根据range links提取genome中的片段

```bash
Usage: rangeops create [options] <infiles>
      
		Options:
				-o --outfile     
			Output filename. [stdout] for screen.
				-g --genome      
			Reference genome file.
				-n --name        
			Default name for ranges.
```

* Example:

```bash
rangeops create xt/I.connect.tsv -g xt/genome.fa -o stdout (App::Rangeops)
rangeops create xt/I.connect.tsv -g xt/genome.fa --name S288c -o stdout (App::Rangeops)
```

* Explanation:

I.connect.tsv:

```bash
I(+):1-100	I(-):1-100
I(+):1000	I(-):1000
```

genome.fa:

```bash
>I
ccacaccacacccacacacccacacaccacaccacacaccacaccacacccacacacacacaTCCTAACACTACCCTAAC
ACAGCCCTAATCTAACCCTGGCCAACCTGTCTCTCAACTTACCCTCCATTACCCTGCCTCCACTCGTTACCCTGTCCCAT
TCAACCATACCACTCCGAACCACCATCCATCCCTCTACTTACTACCACTCACCCACCGTTACCCTCCAATTACCCATATC
CAACCCACTGCCACTTACCCTACCATTACCCTACCATCCACCATGACCTACTCACCATACTGTTCTTCTACCCACCATAT
TGAAACGCTAACAAATGATCGTAAATAACACACACGTGCTTACCCTACCACTTTATACCACCACCACATGCCATACTCAC
CCTCACTTGTATACTGATTTTACGTACGCACACGGATGCTACAGTATATACCATCTCAAACTTACCCTACTCTCAGATTC
CACTTCACTCCATGGCCCATCTCTCACTGAATCAGTACCAAATGCACTCACATCATTATGCACGGCACTTGCCTCAGCGG
TCTATACCCTGTGCCATTTACCCATAACGCCCATCATTATCCACATTTTGATATCTATATCTCATTCGGCGGTCCCAAAT
ATTGTATAACTGCCCTTAATACATACGTTATACCACTTTTGCACCATATACTTACCACTCCATTTATATACACTTATGTC
AATATTACAGAAAAATCCCCACAAAAATCACCTAAACATAAAAATATTCTACTTTTCAACAATAATACATAAACATATTG
GCTTGTGGTAGCAACACTATCATGGTATCACTAACGTAAAAGTTCCTCAATATTGCAATTTGCTTGAACGGATGCTATTT
CAGAATATTTCGTACTTACACAGGCCATACATTAGAATAATATGTCACATCACTGTCGTAACACTCTTTATTCACCGAGC
AATAATACGGTAGTGGCTCAAACTCATGCGGGTGCTATGATACAATTATATCTTATTTCCATTCCCATATGCTAACCGCA
......

```

经过`rangeops create xt/I.connect.tsv -g xt/genome.fa -o stdout`(App::Rangeops)后可以得到:

```bash
>I(+):1-100
ccacaccacacccacacacccacacaccacaccacacaccacaccacacccacacacacacaTCCTAACACTACCCTAACACAGCCCTAATCTAACCCTG
>I(-):1-100
CAGGGTTAGATTAGGGCTGTGTTAGGGTAGTGTTAGGAtgtgtgtgtgtgggtgtggtgtggtgtgtggtgtggtgtgtgggtgtgtgggtgtggtgtgg

>I(+):1000
A
>I(-):1000
T
```

经过`rangeops create xt/I.connect.tsv -g xt/genome.fa --name S288c -o stdout`(App::Rangeops)后可以得到:

```bash
>S288c.I(+):1-100
ccacaccacacccacacacccacacaccacaccacacaccacaccacacccacacacacacaTCCTAACACTACCCTAACACAGCCCTAATCTAACCCTG
>S288c.I(-):1-100
CAGGGTTAGATTAGGGCTGTGTTAGGGTAGTGTTAGGAtgtgtgtgtgtgggtgtggtgtggtgtgtggtgtggtgtgtgggtgtgtgggtgtggtgtgg

>S288c.I(+):1000
A
>S288c.I(-):1000
T
```

ps: 使用create命令前需要先使用`homebrew`安装samtools，`brew install homebrew/science/samtools-0.1`

### 5. filter

Filter links by numbers of ranges or length difference

It's assumed that all ranges in input files are valid

根据range的数量和长度进行筛选

```bash
Usage: rangeops filter [options] <infiles>

		Options:
				--outfile  -o
      Output filename. [stdout] for screen.
      	--number  -n     
      Numbers of ranges, a valid IntSpan runlist.
				--ratio  -r      
			Ratio of lengths differences. The suggested value is [0.8]
```

* Example:

```bash
rangeops filter t/II.connect.tsv -n 2 -o stdout (App::Rangeops)
rangeops filter t/II.connect.tsv -n 3 -r 0.99 -o stdout (App::Rangeops)
```

* Explanation:

II.connect.tsv:

```bash
II(+):1990-5850	VI(+):892-4685	XII(+):7326-11200
II(+):810776-812328	XIII(-):6395-7947	XIV(-):7479-9033
II(+):1-2018	XII(+):204-2215
II(+):300165-301260	IV(+):471852-472948
II(+):477671-479048	XVI(+):700594-701971
II(+):804880-813096	VII(+):1076129-1084340
```

经过`rangeops filter t/II.connect.tsv -n 2 -o stdout`(App::Rangeops)后可以得到：

```bash
II(+):1-2018	XII(+):204-2215
II(+):300165-301260	IV(+):471852-472948
II(+):477671-479048	XVI(+):700594-701971
II(+):804880-813096	VII(+):1076129-1084340	
```

经过`rangeops filter t/II.connect.tsv -n 3 -r 0.99 -o stdout`(App::Rangeops)后可以得到：

```bash
II(+):810776-812328	XIII(-):6395-7947	XIV(-):7479-9033	
```

### 6. merge

Merge overlapped ranges via overlapping graph

将重复的ranges进行合并

```bash
Usage: jrange merge [options] <infiles>

		Options:
				--coverage  -c
			When larger than this ratio, merge ranges.
			Default: 0.95
				--outfile  -o
			Output filename. [stdout] for screen.
				--verbose  -v
			Verbose mode.
 			Default: false
```

* Example:

```bash
jrange merge t/II.links.tsv -o stdout
rangeops merge t/II.links.tsv -o stdout (App::Rangeops)
```

* Explanation:

II.links.tsv:

```bash
II(+):1-2018	XII(+):204-2215	+
II(+):144228-145732	II(-):144228-145732	-
II(+):1990-5850	II(+):2026-5850	+
II(+):1990-5850	XII(+):7326-11200	+
II(+):2026-5850	II(+):1990-5850	+
II(+):2026-5850	VI(+):892-4684	+
II(+):2026-5850	XII(+):7326-11200	+
II(+):300165-301260	IV(+):471852-472948	+
II(+):429496-430989	II(+):429504-430965	+
II(+):429504-430965	II(+):429496-430989	+
II(+):477671-479048	XVI(+):700594-701971	+
II(+):658738-662234	II(-):658738-662234	-
II(+):804880-813096	VII(+):1076129-1084340	+
II(+):806179-808955	VII(+):1077427-1080204	+
II(+):810776-812328	XIII(-):6395-7947	+
II(+):810776-812328	XIV(-):7479-9033	+
II(-):144228-145732	II(+):144228-145732	-
II(-):658738-662234	II(+):658738-662234	-
IV(+):471852-472948	II(+):300165-301260	+
VI(+):892-4684	II(+):2026-5850	+
VI(+):893-4685	II(+):2026-5850	+
VII(+):1076129-1084340	II(+):804880-813096	+
VII(+):1077427-1080204	II(+):806179-808955	+
XII(+):204-2215	II(+):1-2018	+
XII(+):7326-11200	II(+):1990-5850	+
XII(+):7326-11200	II(+):2026-5850	+
XIII(-):6395-7947	II(+):810776-812328	+
XIV(-):7479-9033	II(+):810776-812328	+
XVI(+):700594-701971	II(+):477671-479048	+
```

经过`jrange merge t/II.links.tsv -o stdout`后可以得到：

```bash
II(-):144228-145732	II(+):144228-145732
II(+):2026-5850	II(+):1990-5850
II(+):429504-430965	II(+):429496-430989
II(-):658738-662234	II(+):658738-662234
VI(+):892-4684	VI(+):892-4685
VI(+):893-4685	VI(+):892-4685
```

### 7. sort

Replace ranges within links, incorporate hit strands and remove nested links

将ranges进行排序，并且去除重复的部分

```bash
Usage: jrange sort [options] <infiles>

		Options:
				--outfile, -o
			Output filename. [stdout] for screen.

```

* Example:

```bash
jrange sort t/II.links.tsv -o stdout
rangeops sort t/II.links.tsv -o stdout (App::RL)  		
```

* Explanation:

II.links.tsv:

```bash
II(+):1-2018	XII(+):204-2215	+
II(+):144228-145732	II(-):144228-145732	-
II(+):1990-5850	II(+):2026-5850	+
II(+):1990-5850	XII(+):7326-11200	+
II(+):2026-5850	II(+):1990-5850	+
II(+):2026-5850	VI(+):892-4684	+
II(+):2026-5850	XII(+):7326-11200	+
II(+):300165-301260	IV(+):471852-472948	+
II(+):429496-430989	II(+):429504-430965	+
II(+):429504-430965	II(+):429496-430989	+
II(+):477671-479048	XVI(+):700594-701971	+
II(+):658738-662234	II(-):658738-662234	-
II(+):804880-813096	VII(+):1076129-1084340	+
II(+):806179-808955	VII(+):1077427-1080204	+
II(+):810776-812328	XIII(-):6395-7947	+
II(+):810776-812328	XIV(-):7479-9033	+
II(-):144228-145732	II(+):144228-145732	-
II(-):658738-662234	II(+):658738-662234	-
IV(+):471852-472948	II(+):300165-301260	+
VI(+):892-4684	II(+):2026-5850	+
VI(+):893-4685	II(+):2026-5850	+
VII(+):1076129-1084340	II(+):804880-813096	+
VII(+):1077427-1080204	II(+):806179-808955	+
XII(+):204-2215	II(+):1-2018	+
XII(+):7326-11200	II(+):1990-5850	+
XII(+):7326-11200	II(+):2026-5850	+
XIII(-):6395-7947	II(+):810776-812328	+
XIV(-):7479-9033	II(+):810776-812328	+
XVI(+):700594-701971	II(+):477671-479048	+
```

经过sort后可以得到：

```bash
II(+):1-2018	XII(+):204-2215	+
II(+):1990-5850	II(+):2026-5850	+
II(+):1990-5850	XII(+):7326-11200	+
II(+):2026-5850	VI(+):892-4684	+
II(+):2026-5850	VI(+):893-4685	+
II(+):2026-5850	XII(+):7326-11200	+
II(+):144228-145732	II(-):144228-145732	-
II(+):300165-301260	IV(+):471852-472948	+
II(+):429496-430989	II(+):429504-430965	+
II(+):477671-479048	XVI(+):700594-701971	+
II(+):658738-662234	II(-):658738-662234	-
II(+):804880-813096	VII(+):1076129-1084340	+
II(+):806179-808955	VII(+):1077427-1080204	+
II(+):810776-812328	XIII(-):6395-7947	+
II(+):810776-812328	XIV(-):7479-9033	+
```

## benchmark测试

* Preparation

a. 在 http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.egateam%22%20AND%20a%3A%22jrange%22 下载 jrange-0.0.3-jar-with-dependencies.jar，目录放在~/benchmark/target中

b. 使用cpanm --local-lib=~/perl5 local::lib && eval $(perl -I ~/perl5/lib/perl5/ -Mlocal::lib)命令安装模块

* Command
```bash
bash run.sh
```

* OSX 10.12.3, 4 GHz Intel Core i7, java version "1.8.0_121"

```bash
==> jrange merge lastz blast
        3.75 real         4.59 user         0.49 sys
1487568896  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    367212  page reclaims
       217  page faults
         0  swaps
         0  block input operations
        11  block output operations
         0  messages sent
         0  messages received
         1  signals received
        95  voluntary context switches
      4512  involuntary context switches
==> App::Rangeops merge lastz blast
      165.56 real       490.48 user         1.07 sys
 100143104  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    210522  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         4  block output operations
       140  messages sent
       131  messages received
         0  signals received
       151  voluntary context switches
    139022  involuntary context switches
==> jrange clean sort.clean
        2.49 real         3.90 user         0.35 sys
 933019648  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    231192  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         3  block output operations
         0  messages sent
         0  messages received
         1  signals received
         1  voluntary context switches
      6169  involuntary context switches
==> App::Rangeops clean sort.clean
       67.42 real        67.31 user         0.07 sys
 102846464  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
     32084  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         2  block output operations
         0  messages sent
         0  messages received
         0  signals received
         5  voluntary context switches
      3820  involuntary context switches
==> jrange clean bundle sort.clean
        5.07 real         6.99 user         0.52 sys
1494265856  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
    370013  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         6  block output operations
         0  messages sent
         0  messages received
         3  signals received
         0  voluntary context switches
     10485  involuntary context switches
==> App::Rangeops clean bundle sort.clean
      107.66 real       107.26 user         0.23 sys
 110977024  maximum resident set size
         0  average shared memory size
         0  average unshared data size
         0  average unshared stack size
     34097  page reclaims
         0  page faults
         0  swaps
         0  block input operations
         6  block output operations
         0  messages sent
         0  messages received
         0  signals received
         4  voluntary context switches
     23189  involuntary context switches
```
