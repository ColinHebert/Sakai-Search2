package uk.ac.ox.oucs.search2.compatibility.result;

import org.sakaiproject.search.api.SearchList;
import org.sakaiproject.search.api.SearchResult;
import uk.ac.ox.oucs.search2.result.SearchResultList;

import java.util.*;

/**
 * Transforms a result list of Search2 into a result of Search1.
 *
 * @author Colin Hebert
 */
public class Search1SearchList implements SearchList {
    SearchResultList searchResults;

    public Search1SearchList(SearchResultList searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public int getStart() {
        return (int) searchResults.getStartCurrentSelection();
    }

    @Override
    public int getFullSize() {
        return (int) searchResults.getNumberResultsFound();
    }

    @Override
    public int size() {
        return searchResults.size();
    }

    @Override
    public boolean isEmpty() {
        return searchResults.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return searchResults.contains(o);
    }

    @Override
    public Iterator<SearchResult> iterator() {
        return new SearchResultIterator(searchResults.listIterator());
    }

    @Override
    public Object[] toArray() {
        return searchResults.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return searchResults.toArray(a);
    }

    @Override
    public boolean add(SearchResult searchResult) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        return searchResults.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return searchResults.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends SearchResult> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends SearchResult> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return searchResults.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return searchResults.retainAll(c);
    }

    @Override
    public void clear() {
        searchResults.clear();
    }

    @Override
    public boolean equals(Object o) {
        return searchResults.equals(o);
    }

    @Override
    public int hashCode() {
        return searchResults.hashCode();
    }

    @Override
    public SearchResult get(int index) {
        return new Search1SearchResult(searchResults.get(index));
    }

    @Override
    public SearchResult set(int index, SearchResult element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, SearchResult element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SearchResult remove(int index) {
        return new Search1SearchResult(searchResults.remove(index));
    }

    @Override
    public int indexOf(Object o) {
        return searchResults.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return searchResults.lastIndexOf(o);
    }

    @Override
    public ListIterator<SearchResult> listIterator() {
        return new SearchResultIterator(searchResults.listIterator());
    }

    @Override
    public ListIterator<SearchResult> listIterator(int index) {
        return new SearchResultIterator(searchResults.listIterator(index));
    }

    @Override
    public List<SearchResult> subList(int fromIndex, int toIndex) {
        List<SearchResult> subSearchResults = new ArrayList<SearchResult>();
        for (uk.ac.ox.oucs.search2.result.SearchResult searchResult : searchResults.subList(fromIndex, toIndex))
            subSearchResults.add(new Search1SearchResult(searchResult));
        return Collections.unmodifiableList(subSearchResults);
    }

    @Override
    public Iterator<SearchResult> iterator(int startAt) {
        Iterator<SearchResult> iterator = iterator();
        for (int i = 0; i < startAt && iterator.hasNext(); i++)
            iterator.next();
        return iterator;
    }

    private final class SearchResultIterator implements ListIterator<SearchResult> {
        private final ListIterator<uk.ac.ox.oucs.search2.result.SearchResult> iterator;

        private SearchResultIterator(ListIterator<uk.ac.ox.oucs.search2.result.SearchResult> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public SearchResult next() {
            return new Search1SearchResult(iterator.next());
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        @Override
        public SearchResult previous() {
            return new Search1SearchResult(iterator.previous());
        }

        @Override
        public int nextIndex() {
            return iterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return iterator.previousIndex();
        }

        @Override
        public void remove() {
            iterator.remove();
        }

        @Override
        public void set(SearchResult searchResult) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(SearchResult searchResult) {
            throw new UnsupportedOperationException();
        }
    }
}
