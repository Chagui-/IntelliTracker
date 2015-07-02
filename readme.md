# Intellitracker

## Breaf story
The initial purpose of this android app was to track my international packages, as I was begining to have a lot of them. I tried the other apps on the market but i didnt liked them because they were slow, or they asked a lot of info on the item in order to perfor the track, like origin county courier and end country courier, something that was ridicolous to me at that point in time. I was using the 17track site to track my packages, so I took a peek at their code and figured out what they were doing. I then started developing my own app based on their tracking api.

## Why is it named like that?
Nothing really special, the other names were picked and I that name was the best ones of among the other I had in mind.

## How does the code work?
Well first there is a local database containind all the data structures i use in the app, which are basically 2:
- TrackingItem
- Label
TrackingItem contains the information of the item to track, like the tracking number, the name, the couriers, that status, etc. Each one of this items can have a number of labels attached to them, for easy categorization.
TheItemManager class is encharged of reading and storing the info of the previous classes to the database.

The connection to the 17track server is done in the TrackingTask class, which is the worker on a work pool on the TrackingManager class.

To recognize the courier, the app analizes the tracking number with all of the 23 couriers classes and their tracking number patterns. The couriers supported are: Aramex, BQC, Buylogic, DHL, DPD, DPD UK, Eshipper, Fedex, FLYT, Global Postal, GLS, HHEXP, One World, RETS, RUSH, Ruston, SFExpress, TNT, Toll, UPS, XRU, YANWEN, YUNPOST, and 007EX. Sometimes multiple couriers have the same tracking number pattern, so the app searches among all these and decides based on if the item is found and the track hasnt expired. Even then its still posible to have some collition, and the app doesnt manage collition just yet.

About the UI, I dont really remember how I did it, but im sure it uses some black magic, duct tape, and probably useless code blocks that don't anything.