resolvers += "HMRC-open-artefacts-maven" at "https://open.artefacts.tax.service.gov.uk/maven2"
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)
resolvers += Resolver.jcenterRepo

addSbtPlugin("com.github.sbt"    % "sbt-release"            % "1.0.15")
addSbtPlugin("org.playframework" % "sbt-plugin"             % "3.0.2")
addSbtPlugin("uk.gov.hmrc"       % "sbt-distributables"     % "2.5.0")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"            % "0.6.3")
addSbtPlugin("org.scoverage"     % "sbt-scoverage"          % "2.0.11")
addSbtPlugin("org.scalastyle"    %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("uk.gov.hmrc"       % "sbt-auto-build"         % "3.21.0")

// To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always