//package alison.fivethingskotlin.util
//
//import alison.fivethingskotlin.R
//import android.support.v4.view.ViewPager
//import android.view.View
//import android.widget.TextView
//
//
//class IntroPageTransformer : ViewPager.PageTransformer {
//
//    override fun transformPage(page: View, position: Float) {
//
//        val pagePosition = page.tag
//
//        // Here you can do all kinds of stuff, like get the
//        // width of the page and perform calculations based
//        // on how far the user has swiped the page.
//        val pageWidth = page.width
//        val pageWidthTimesPosition = pageWidth * position
//        val absPosition = Math.abs(position)
//
//        if (position <= -1.0f || position >= 1.0f) {
//            // The page is not visible. This is a good place to stop
//            // any potential work / animations you may have running.
//        } else if (position == 0.0f) {
//            // The page is selected. This is a good time to reset Views
//            // after animations as you can't always count on the PageTransformer
//            // callbacks to match up perfectly.
//        } else {
//            // The page is currently being scrolled / swiped. This is
//            // a good place to show animations.
//
//            // Title fades as it scrolls out
//            val title = page.findViewById(R.id.title) as TextView
//            title.alpha = 1.0f - absPosition
//
//            // Description slowly moves down and out of the screen
//            val description = page.findViewById(R.id.description) as TextView
//            description.translationY = -pageWidthTimesPosition / 2f
//            description.alpha = 1.0f - absPosition
//
//            // Now, we want the image to move to the right,
//            // i.e. in the opposite direction of the rest of the
//            // content while fading out
//            val computer = page.findViewById(R.id.computer)
//
//            // We're attempting to create an effect for a View
//            // specific to one of the pages in our ViewPager.
//            // In other words, we need to check that we're on
//            // the correct page and that the View in question
//            // isn't null.
//            if (pagePosition == 0 && computer != null) {
//                computer!!.setAlpha(1.0f - absPosition)
//                computer!!.setTranslationX(-pageWidthTimesPosition * 1.5f)
//            }
//
//            // Finally, it can be useful to know the direction
//            // of the user's swipe - if we're entering or exiting.
//            // This is quite simple:
//            if (position < 0) {
//                // Create your out animation here
//            } else {
//                // Create your in animation here
//            }
//        }
//    }
//
//}