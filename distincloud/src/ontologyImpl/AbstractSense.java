package ontologyImpl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ontologyInterface.SemRelation;

public abstract class AbstractSense implements
    Serializable {
  private static final long serialVersionUID =
      3216819444449973663L;

  public static class RelatednessInfo {
    protected int maxRelatedness;
    protected List<AbstractSense> destWordSenses;
    protected int bestRelatedness;
    protected Set<SemRelation> semanticUpwardRelations;
    protected Set<SemRelation> semanticDownwardRelations;
    protected Set<SemRelation> semanticHorizontalRelations;
    protected List<AbstractSense> previousVisits;
  }

  protected boolean active;
  protected String definition;
  protected transient Map<SemRelation, List<AbstractSense>> activeLinks;

  public AbstractSense(boolean active, String definition) {
    super();
    this.active = active;
    this.definition = definition;
    initializeActiveLinks();
  }

  public AbstractSense() {
    super();
    initializeActiveLinks();
  }

  private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    initializeActiveLinks();
  }

  private void initializeActiveLinks() {
    activeLinks =
        new HashMap<SemRelation, List<AbstractSense>>();
    for (SemRelation semRelation : SemRelation.values())
      activeLinks.put(
          semRelation, new ArrayList<AbstractSense>());
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public abstract AbstractWord getWord();

  public String getDefinition() {
    return definition;
  }

  public void setSenseDefinition(String definition) {
    this.definition = definition;
  }

  public void activateDeactivate(boolean activate)
      throws IOException {
    setActive(activate);
    getWord().getOntology().rebuildActiveSenseLinks();
  }

  public List<AbstractSense> getActiveLinks(
      Set<SemRelation> semanticRelations) {
    List<AbstractSense> result =
        new ArrayList<AbstractSense>();
    for (SemRelation semRelation : semanticRelations)
      result.addAll(activeLinks.get(semRelation));
    return result;
  }

  public List<AbstractSense> getActiveLinks(
      SemRelation semRelation) {
    return activeLinks.get(semRelation);
  }

  protected abstract List<AbstractSense> getAllLinks(
      SemRelation semRelation);

  public void addActiveLink(SemRelation semRelation,
      AbstractSense link) {
    if (!activeLinks.get(
        semRelation).contains(
        link)) {
      activeLinks.get(
          semRelation).add(
          link);
      link.addActiveLink(
          semRelation.oppositeRel(), this);
    }
  }

  public void rebuildActiveLinksLists() {
    for (SemRelation semRelation : SemRelation.values()) {
      activeLinks.get(
          semRelation).clear();
      for (AbstractSense link : getAllLinks(semRelation))
        // We consider that the initial sense is a visited one in order not add
        // reflexive links for this sense
        link.buildActiveLinksList(
            0, semRelation, new ArrayList<AbstractSense>(
                Arrays.asList(this)),
            activeLinks.get(semRelation));
    }
  }

  protected void buildActiveLinksList(int depth,
      SemRelation semRelation,
      List<AbstractSense> visitedSenses,
      List<AbstractSense> activeLinksList) {
    if ((depth == 4) || (visitedSenses.contains(this)))
      return;
    else
      if (this.isActive()) {
        if (!activeLinksList.contains(this))
          activeLinksList.add(this);
      } else {
        visitedSenses.add(this);
        for (AbstractSense link : getAllLinks(semRelation))
          link.buildActiveLinksList(
              depth + 1, semRelation, visitedSenses,
              activeLinksList);
        visitedSenses.remove(this);
      }
  }

  public int getRelatedness(int state, int distance,
      RelatednessInfo relatednessInfo) {
    int result =
        relatednessInfo.maxRelatedness - distance;
    if (result < 0)
      return -1;
    if (relatednessInfo.destWordSenses.contains(this)) {
      if (result > relatednessInfo.bestRelatedness)
        relatednessInfo.bestRelatedness = result;
      return result;
    }
    if ((result == 0) ||
        (result <= relatednessInfo.bestRelatedness))
      return -1;
    for (AbstractSense aPreviousVisit : relatednessInfo.previousVisits)
      if (aPreviousVisit == this)
        return -1;
    relatednessInfo.previousVisits.add(this);
    if (state == 0) {
      result = -1;
      int tempResult;
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticUpwardRelations)) {
        tempResult = link.getRelatedness(
            1, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticDownwardRelations)) {
        tempResult = link.getRelatedness(
            3, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticHorizontalRelations)) {
        tempResult = link.getRelatedness(
            2, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      relatednessInfo.previousVisits.remove(this);
      return result;
    }
    if (state == 1) {
      result = -1;
      int tempResult;
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticUpwardRelations)) {
        tempResult = link.getRelatedness(
            4, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticDownwardRelations)) {
        tempResult = link.getRelatedness(
            6, distance,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticHorizontalRelations)) {
        tempResult = link.getRelatedness(
            2, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      relatednessInfo.previousVisits.remove(this);
      return result;
    }
    if (state == 2) {
      result = -1;
      int tempResult;
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticDownwardRelations)) {
        tempResult = link.getRelatedness(
            5, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticHorizontalRelations)) {
        tempResult = link.getRelatedness(
            2, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      relatednessInfo.previousVisits.remove(this);
      return result;
    }
    if (state == 3) {
      result = -1;
      int tempResult;
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticDownwardRelations)) {
        tempResult = link.getRelatedness(
            3, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticHorizontalRelations)) {
        tempResult = link.getRelatedness(
            7, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      relatednessInfo.previousVisits.remove(this);
      return result;
    }
    if (state == 4) {
      result = -1;
      int tempResult;
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticUpwardRelations)) {
        tempResult = link.getRelatedness(
            4, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticDownwardRelations)) {
        tempResult = link.getRelatedness(
            5, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticHorizontalRelations)) {
        tempResult = link.getRelatedness(
            2, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      relatednessInfo.previousVisits.remove(this);
      return result;
    }
    if (state == 5) {
      result = -1;
      int tempResult;
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticDownwardRelations)) {
        tempResult = link.getRelatedness(
            5, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      relatednessInfo.previousVisits.remove(this);
      return result;
    }
    if (state == 6) {
      result = -1;
      int tempResult;
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticDownwardRelations)) {
        tempResult = link.getRelatedness(
            5, distance + 2,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      relatednessInfo.previousVisits.remove(this);
      return result;
    }
    if (state == 7) {
      result = -1;
      int tempResult;
      for (AbstractSense link : this
          .getActiveLinks(relatednessInfo.semanticHorizontalRelations)) {
        tempResult = link.getRelatedness(
            7, distance + 1,
            relatednessInfo);
        if (tempResult > result)
          result = tempResult;
      }
      relatednessInfo.previousVisits.remove(this);
      return result;
    }
    relatednessInfo.previousVisits.remove(this);
    return 0;
  }
}