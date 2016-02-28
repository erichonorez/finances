package org.svomz.apps.finances.ports.adapters.web.controllers;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by eric on 28/02/16.
 */
public class AccountForm {

  @NotNull
  @Size(min = 1, max = 255)
  private String description;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
