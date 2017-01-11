Test files. These represent complete responses from the Metadata API.

Test publications:

 - 10.5555/up-up : "Trebuchets: Up up and Away"
   Author: Josiah Carberry.
 - 10.5555/catapult: "Catapults Considered Harmful"
   Author mysteriously missing.
   Domain-exclusive enabled.
 - 10.5555/withdrawal-catapult: "WITHDRAWAL: Catapults Considered Harmful"
 - 10.6666/trebuchet: "Trebuchets or Catapults? Catapults!"
 - 10.6666/retraction-trebuchet: "RETRACTION: Trebuchets or Catapults? Catapults!"

Clinical Trials:

 - XX1234: "Study of Catapults vs Trebuchets in Treating Seige Mentality"

Relationships:

 - 10.5555/up-up
   - references CTN XX1234
 - 10.6666/trebuchet
   - references CTN XX1234 BUT with an invalid relationType
 - 10.5555/withdrawal-catapult 
  - withdraws 10.5555/catapult
  - has no CTNs
 - 10.6666/retraction-trebuchet
  - withdraws 10.6666/trebuchet

