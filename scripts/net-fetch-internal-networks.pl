use strict;
use Data::Dumper;
use Carp;

use Getopt::Long;
use Bio::KBase::KBaseNetworksService::Client;

#This API is for demo use only. A standard one will take place by the end Jan, 2014
#example:
#grep 'kb' genelistfile|perl /kb/dev_container/modules/networks/scripts/net_fetch_internal_networks.pl 'kb|g.3899'

my $url       = "http://kbase.us/services/networks";
#my $url="http://140.221.85.171:7064/KBaseNetworksRPC/networks";
my $usage = "Usage: echo genelist |net_fetch_internal_networks 'kb|g.3899' 'GENE_GENE'\n";
my $help       = 0;
my $version    = 0;

GetOptions("help"       => \$help,
           "version"    => \$version,
           "url=s"      => \$url) or die $usage;

if($help){
print "This command is used to query the internal networks for a list of genes.\nThe genes should be KBase gene loucs ID\n";
print "We suggest the number of genes is less than 30 due to a large collection of network datasets\n";
print "\nUsage:\n";
print "echo genelist |net_fetch_internal_networks 'kb|g.3899' 'GENE_GENE'\n\n";
print "genelist is a list of genes, such as:'kb|g.3899.locus.534' 'kb|g.3899.locus.19668' 'kb|g.3899.locus.6286' 'kb|g.3899.locus.13048' 'kb|g.3899.locus.23790'\n";
print "Contact: Fei He\n";
exit(0);
}

if($version)
{
    print "net_fetch_internal_network version 1.0\n";
    print "Copyright (C) 2012 KBase Network Team\n";
    print "License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.\n";
    print "This is free software: you are free to change and redistribute it.\n";
    print "There is NO WARRANTY, to the extent permitted by law.\n";
    print "\n";
    print "Written by Shinjae Yoo\n";
    exit(0);
}

die $usage unless @ARGV == 2;


my $oc = Bio::KBase::KBaseNetworksService::Client->new($url);
my $taxons=$ARGV[0];
my $edge_types = $ARGV[1];
my $res1 = $oc->taxon2datasets($taxons);
my @datasetIds;
my $i=0;
my $dataset2name;
my $dataset2des;
my $dataset2type;
my $dataset2source;
foreach (@{$res1}){
    $i++;
    push @datasetIds,$_->{'id'};
    $dataset2name->{$_->{'id'}}=$_->{'name'};
    $dataset2source->{$_->{'id'}}=$_->{'source_reference'};
    $dataset2type->{$_->{'id'}}=$_->{'network_type'};
    $dataset2des->{$_->{'id'}}=$_->{'description'};
}

my @input = <STDIN>;                                                                             
my $istr = join(" ", @input);                                                                    
$istr =~ s/[,]/ /g;
@input = split /\s+/, $istr;          
my @edgeTypes = split/,/, $edge_types;
my $results =$oc->build_internal_network(\@datasetIds, \@input, \@edgeTypes);

my %nodes = ();
foreach my $hr (@{$results->{'nodes'}}) {
  $nodes{$hr->{'id'}} = [$hr->{'entity_id'}, $hr->{'name'}];
}

my $rec_node;
my $rec_edge;
my $density;
foreach my $hr (@{$results->{'edges'}}) {
  my $id1 = $nodes{$hr->{'node_id1'}}[0];
  my $id2 = $nodes{$hr->{'node_id2'}}[0];
  my $nm1 = $nodes{$hr->{'node_id1'}}[1];
  my $nm2 = $nodes{$hr->{'node_id2'}}[1];
  my $strength = $hr->{'strength'};
  my $ds  = $hr->{'dataset_id'};
  my $enm = $hr->{'name'};
  $rec_node->{$ds}->{$id1}=1;
  $rec_node->{$ds}->{$id2}=1;
  $rec_edge->{$ds}->{$id1.$id2}=1;

}
my %no_node;
my %no_edge;
foreach (keys %{$rec_node}){
	foreach my $in(keys %{$rec_node->{$_}}){
		$no_node{$_}++;
	}
}

foreach (keys %{$rec_edge}){
	foreach my $in(keys %{$rec_edge->{$_}}){
		$no_edge{$_}++;
	}
}
foreach (keys %no_node){
	$density->{$_}=sprintf "%.2f",($no_edge{$_}*2)/$no_node{$_};
}
#start to generate core-table for narrative2 
print "Dataset\tNo. of nodes\tNo. of edges\tdensity\tDescription\tType\tSource\n";
foreach (sort {$no_node{$b} <=> $no_node{$a} } keys %no_node){
  
  #col1
  my $c1=$_;
  #col2
  my $c2=$no_node{$_};
  #col3
  my $c3=$no_edge{$_};
  #col4
  my $c4=$density->{$_};
  #col5
  my $c5=$dataset2name->{$_}."-".$dataset2des->{$_};
  #col6
  my $c6=$dataset2type->{$_};
  #col7
  my $c7=$dataset2source->{$_};
  
  #output core-table by line
  print "$c1\t$c2\t$c3\t$c4\t$c5\t$c6\t$c7\n";
  
}
