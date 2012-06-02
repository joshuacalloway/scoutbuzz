resolvers += Resolver.url("scalasbt releases", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("org.scala-sbt" % "sbt-android-plugin" % "0.6.1")

libraryDependencies ++= Seq(
   "net.sf.proguard" % "proguard-base" % "4.7"
)