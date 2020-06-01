# RTP-PACKET

A library for decoding/manipulation of RTP packets as per RFC 3550. 

## Version History

| Version  | Date  | CHanges
|---------------|----------------|--------|
| 2.0.0    |   May 2020   |  Added support for RTCP as per RFC 3550   |
| 1.0.0    |   March 2020   |  Initial Release - RTP support as per RFC 3550  |


## Getting Started


### Maven dependancy

In order to use this library, simply include the maven dependancy as shown below.

```
<dependency>
    <groupId>org.vidtec</groupId>
    <artifactId>rtp-packet</artifactId>
    <version>2.0.0</version>
</dependency>
```


## Features

The rtp-packet library incorporates the following features:

 - Builder style packet instance creation.
 - Reading packets from byte[], DatagramPacket
 - Writing packets to byte[], DatagramPacket
 
General properties of RTPPacket:

 - All packet objects are fully validated against RFC 3550 during instantiation
 - All packet objects are immutable
 - Getter methods to extract data points
 - Automatic handling of padding
 - Automatic handling of header extensions
   


## Usage - RTP

All packet creation and reading undergoes complete validation. It is not possible to create an RTP packet that is not valid according to spec.

## Creating RDP Packets

In order to create a packet by hand, you can use the builder pattern to create a packet.

```

final RTPPacket p = RTPPacket.builder()
					  .withMarker()
		 			  .withRequiredHeaderFields(<packet_type, <seq_num>, <ssrc>, <timestamp>)	
					  .withCsrcIdentifiers(... <csrcs> ...)
					  .withHeaderExtension(<profile>, <extension_header>)
					  .withPayload(<payload>)
					  .build();
					  
```

**NB:** The ```build()``` method will throw an ```IllegalArgumentException``` if any of the data supplied would lead to creating of an invalid packet (according to RFC 3550).



## Reading RDP Packets


To create a new packet from a ```byte[]```

```

final RTPPacket p = RTPPacket.fromByteArray(<byte_array>);

```

**NB:** This method will throw an ```IllegalArgumentException``` if any of the data supplied would lead to creating of an invalid packet (according to RFC 3550).


To create a new packet from a ```DatagramPacket```

```

final RTPPacket p = RTPPacket.romDatagramPacket(<datagram_packet>);

```

**NB:** This method will throw an ```IllegalArgumentException``` if any of the data supplied would lead to creating of an invalid packet (according to RFC 3550).


## Writing RDP Packets



To create a new packet from a ```byte[]```

```

final RTPPacket p = RTPPacket.fromByteArray(<byte_array>);

```


To create a new packet from a ```DatagramPacket```

```

final RTPPacket p = RTPPacket.fromDatagramPacket(<datagram_packet>);

```

## Usage - RTCP

All packet creation and reading undergoes complete validation. It is not possible to create an RTCP packet that is not valid according to spec.

## Compound Packets

## Creating Compound Packets

In order to create a packet by hand, you can use the builder pattern to create a packet.

```

final RTCPPackets p = RTCPPackets.builder()
				.withPacket( ... <RTCPPackets> ... )								
				.build();
				
```
or the shorthand version
```

final RTCPPackets p = RTCPPackets.buildWithPackets( ... <RTCPPackets> ... );

```

## Reading Compound Packets


To create a new packet from a ```byte[]```

```

final RTCPPackets p = RTCPPackets.fromByteArray(<byte_array>);

```

**NB:** This method will throw an ```IllegalArgumentException``` if any of the data supplied would lead to creating of an invalid packet (according to RFC 3550).


To create a new packet from a ```DatagramPacket```

```

final RTCPPackets p = RTCPPackets.romDatagramPacket(<datagram_packet>);

```

**NB:** This method will throw an ```IllegalArgumentException``` if any of the data supplied would lead to creating of an invalid packet (according to RFC 3550).


## Writing Compound Packets


To create a new packet from a ```byte[]```

```

final RTCPPackets p = RTCPPackets.fromByteArray(<byte_array>);

```


To create a new packet from a ```DatagramPacket```

```

final RTCPPackets p = RTCPPackets.fromDatagramPacket(<datagram_packet>);

```







## Versioning

This project uses [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/gareth-floodgate/rtp-packet/tags). 

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details



