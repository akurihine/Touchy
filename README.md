# Touchy

This is an example of using AdvancedTouchListener. 
TouchyLayout is a custom layout that exends FrameLayout. 
Any view added to it will be moveable.
It uses AdvancedTouchListener to detect rotation, scaling, draging, and tapping.
<br><br><br>
<b>AdvancedTouchListener:</b>
* Includes several custom gesture detectures, including one for rotation
* Brings detection of rotation, scaling, draging, and tapping together into one listener

<br><br>
<b>Some things that TouchyLayout does:</b>
* Makes sure you only interact with one view at a time
* Pushes current view to top
* Allows for selection to accure without direct contact, when using a 2 finger gesture. It uses the focal point between the fingers to make a guess.
* Ignores first contact for a brief moment so two fingure gestures do not get treated like single finger gestures intially
* In the case of several potential canidates for the touch, top wins
* Allows you to add views progmatically like other layouts

<br><br><br>
![ScreenShot](https://media.giphy.com/media/5zbnE73cf6AbE64n3v/giphy.gif)
