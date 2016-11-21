package org.svomz.apps.finances.domain.model.interpreter

import scalaz.NonEmptyList

class ValidationException(errors: NonEmptyList[String]) extends Exception {

}
