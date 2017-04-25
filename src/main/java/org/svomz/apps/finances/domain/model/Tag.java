package org.svomz.apps.finances.domain.model;

import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

/**
 * Created by eric on 23/04/17.
 */
public class Tag {

  private static final String validNamePattern = "[a-zA-Z0-9-]+";

  private final String name;

  public Tag(String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(Pattern.matches(validNamePattern, name));
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Tag tag = (Tag) o;

    return name.equals(tag.name);

  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
