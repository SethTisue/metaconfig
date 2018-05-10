package metaconfig.hocon

import metaconfig.ConfOps
import metaconfig.ConfShow
import metaconfig.Configured
import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalameta.logger

class ConfProps extends Properties("Conf") {
  import metaconfig.Generators.argConfShow
  property("normalise is idempotent") = forAll { show: ConfShow =>
    val Configured.Ok(conf) = Hocon2Class.gimmeConfig(show.input)
    val isEqual = conf.normalize == conf.normalize.normalize
    if (!isEqual) {
      val diff = ConfOps.diff(conf.normalize, conf.normalize.normalize)
      if (diff.nonEmpty) {
        logger.elem(conf.normalize, conf.normalize.normalize, diff)
      }
    }
    isEqual
  }
}
