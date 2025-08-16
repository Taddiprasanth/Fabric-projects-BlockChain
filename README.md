Fabric Projects BlockChain 


This repository combines Hyperledger Fabric Samples and Java Chaincode Projects into one place for easier management and demonstration.

📌 Repository Structure

Fabric-projects-block/
│                 
├── fabric-samples/              ** # Hyperledger Fabric official sample network **
│   ├── test-network/           **  # Test network scripts (create channel, deploy CC, etc.)    **        
│   ├── chaincode/                **# Sample chaincodes (Go, JavaScript, etc.)**                  
│   └── ...                     
│                
├── fabric-chaincode-java/       ** # Java chaincode implementation **              
│   ├── examples/                                                                                
│   │       └── fabric-contract-example-maven/           ** # Java chaincode example using Maven **                 
│   └── ...  
│                        
└── README.md        **#  Documentation  **                   

🚀 Getting Started
1. Clone the repository:
                     
           git clone https://github.com/Taddiprasanth/Fabric-projects-BlockChain         
           cd Fabric-projects-block

3. Start the Test Network
   Go to the Fabric samples test network:
   
           cd fabric-samples/test-network                       
           ./network.sh up createChannel             

4. Deploy Java Chaincode
From inside test-network:
                  
           ./network.sh deployCC -ccn myasset \                   
           -ccp ../../fabric-chaincode-java/examples/fabric-contract-example-maven \           
           -ccl java         

5. Interact with the Chaincode
                                                       
           peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com \
            --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" \
           -C mychannel -n myasset -c '{"Args":["CreateMyAsset","asset1","blue","10","Tom"]}'

📂 Project Levels

This work was done in Levels as per assignment:

✅ Level 1: Setup Fabric & run test network

✅ Level 2: Deploy and test Java chaincode

⏳ Level 3: (In Progress) Extended features & integration


🔧 Requirements  
        
        Docker & Docker Compose                   
        Git                             
        JDK 11+ and Maven                          
        Hyperledger Fabric binaries (v2.5.12)                          

📌 Notes

Both projects (fabric-samples and fabric-chaincode-java) are preserved inside this repository.





