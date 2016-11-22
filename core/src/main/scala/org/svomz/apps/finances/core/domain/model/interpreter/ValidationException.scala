package org.svomz.apps.finances.core.domain.model.interpreter

import scalaz.NonEmptyList

class ValidationException(errors: NonEmptyList[String]) extends Exception {

}
